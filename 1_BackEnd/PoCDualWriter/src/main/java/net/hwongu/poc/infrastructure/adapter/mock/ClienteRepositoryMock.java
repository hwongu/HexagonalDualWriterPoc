package net.hwongu.poc.infrastructure.adapter.mock;

import net.hwongu.poc.domain.model.Cliente;
import net.hwongu.poc.domain.port.out.ClienteRepository;

/**
 * <strong>Rol Adaptador Mock para el puerto ClienteRepository</strong>
 *
 * <p>
 * Actuo como implementacion en memoria simulada del puerto de salida {@link ClienteRepository}.
 * Sirvo para pruebas rapidas y demostraciones sin depender de una base de datos real.
 * </p>
 *
 * <p>
 * <strong>Proposito arquitectonico</strong><br>
 * Permitir validar el flujo de la capa Application y Domain sin infraestructura,
 * facilitando escenarios de test, demos y desarrollo local.
 * </p>
 *
 * <strong>Nota</strong> Esta implementacion retorna un cliente fijo cuando el id es 1.
 * Para otros valores retorna null para simular ausencia de datos.
 *
 * <p>
 * <strong>GitHub</strong> hwongu
 * </p>
 *
 * @author Henry Wong
 */
public class ClienteRepositoryMock implements ClienteRepository {

    @Override
    public void guardar(Cliente cliente) {
        System.out.println("[MOCK] Guardando en memoria simulada: " + cliente.getNombre());
        System.out.println("[MOCK] ... ¡Guardado!");
    }

    @Override
    public Cliente buscarPorId(Integer id) {
        System.out.println("--- [MOCK] Simulando busqueda ---");
        if (id == 1) return new Cliente(1, "Empresa Demo S.A.", 50000.00);
        return null;
    }

}
