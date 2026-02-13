package net.hwongu.poc.infrastructure.adapter.database;

import net.hwongu.poc.domain.model.Cliente;
import net.hwongu.poc.domain.port.out.ClienteRepository;

import java.sql.*;

/**
 * <strong>Rol Adaptador de infraestructura para repositorio Cloud usando JDBC</strong>
 *
 * <p>
 * Actuo como implementacion concreta del puerto de salida {@link ClienteRepository} para una base de datos Cloud
 * basada en PostgreSQL. Traduzco las operaciones del dominio a sentencias SQL y mapeo resultados a objetos
 * {@link Cliente}.
 * </p>
 *
 * <p>
 * <strong>Proposito arquitectonico</strong><br>
 * Encapsular el detalle de persistencia en la capa Infrastructure, evitando que Application o Domain conozcan JDBC
 * y permitiendo intercambiar esta implementacion por otra tecnologia sin afectar el core.
 * </p>
 *
 * <strong>Nota</strong> Esta POC usa credenciales embebidas y salida por consola. En un escenario real se recomienda
 * externalizar configuracion, usar pool de conexiones, logging y manejo de errores consistente.
 *
 * <p>
 * <strong>GitHub</strong> hwongu
 * </p>
 *
 * @author Henry Wong
 */
public class ClienteRepositoryDbCloud implements ClienteRepository {

    private final String URL = "jdbc:postgresql://localhost:5432/Db_Cloud";
    private final String USER = "postgres";
    private final String PASS = "clave";

    @Override
    public void guardar(Cliente cliente) {
        String sql = "INSERT INTO cliente (razon_social, saldo_actual) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNombre());
            stmt.setDouble(2, cliente.getSaldo());
            stmt.executeUpdate();
            System.out.println("[CLOUD] INSERT exitoso en tabla 'cliente'");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Cliente buscarPorId(Integer id) {
        System.out.println("--- [CLOUD] Conectando a Base de Datos Cloud ---");
        String sql = "SELECT id_cliente, razon_social, saldo_actual FROM cliente WHERE id_cliente = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idEncontrado = rs.getInt("id_cliente");
                    String nombre = rs.getString("razon_social");
                    Double saldo = rs.getDouble("saldo_actual");
                    return new Cliente(idEncontrado, nombre, saldo);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en Cloud: " + e.getMessage());
        }
        return null;
    }

}
