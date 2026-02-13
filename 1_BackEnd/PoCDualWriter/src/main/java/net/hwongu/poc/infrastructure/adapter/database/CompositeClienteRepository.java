package net.hwongu.poc.infrastructure.adapter.database;

import net.hwongu.poc.domain.model.Cliente;
import net.hwongu.poc.domain.port.out.ClienteRepository;

import java.util.Arrays;
import java.util.List;

/**
 * <strong>Rol: Orquestador de Escritura Dual (Dual Writer) mediante Patrón Composite.</strong>
 *
 * <p>
 * Actúo como un proxy transparente que agrupa múltiples implementaciones de persistencia.
 * Cuando el Dominio solicita una operación, <strong>intercepto</strong> la llamada y la delego
 * secuencialmente a todas las fuentes de datos configuradas (Legacy y Cloud).
 * </p>
 *
 * <p>
 * <strong>Propósito Arquitectónico:</strong><br>
 * Desacoplar la lógica de negocio de la estrategia de migración. Gracias a mí, el Servicio de Aplicación
 * ignora que está escribiendo en dos bases de datos simultáneamente.
 * </p>
 *
 * <strong>Nota:</strong> Esta implementación no garantiza atomicidad distribuida (2PC).
 * Si falla la segunda escritura, podría generar inconsistencia eventual.
 *
 * @author Henry Wong
 */
public class CompositeClienteRepository implements ClienteRepository {


    // Lista de repositorios hijos (Cloud, OnPremise, Mock, etc.)
    private final List<ClienteRepository> repositorios;

    public CompositeClienteRepository(ClienteRepository... repositorios) {
        this.repositorios = Arrays.asList(repositorios);
    }

    @Override
    public void guardar(Cliente cliente) {
        System.out.println("\n=== [COMPOSITE] Iniciando escritura múltiple ===");
        // Recorremos cada repositorio y ejecutamos guardar
        for (ClienteRepository repo : repositorios) {
            try {
                repo.guardar(cliente);
            } catch (Exception e) {
                System.err.println("Error guardando en uno de los repositorios: " + e.getMessage());
                return;
            }
        }
        System.out.println("=== [COMPOSITE] Escritura múltiple finalizada ===\n");
    }

    @Override
    public Cliente buscarPorId(Integer id) {
        // En un Composite de escritura, la lectura suele delegarse a una fuente principal.
        // Estrategia: "Read from Master" (Asumimos que el primero de la lista es el Master)
        if (!repositorios.isEmpty()) {
            return repositorios.get(0).buscarPorId(id);
        }
        return null;
    }
}
