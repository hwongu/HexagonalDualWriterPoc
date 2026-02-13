package net.hwongu.poc;

import net.hwongu.poc.application.service.ConsultaSaldoService;
import net.hwongu.poc.application.service.CrearClienteService;
import net.hwongu.poc.domain.port.out.ClienteRepository;
import net.hwongu.poc.infrastructure.adapter.database.ClienteRepositoryDbCloud;
import net.hwongu.poc.infrastructure.adapter.database.ClienteRepositoryDbOnPremise;
import net.hwongu.poc.infrastructure.adapter.database.CompositeClienteRepository;
import net.hwongu.poc.infrastructure.adapter.mock.ClienteRepositoryMock;

import java.util.Scanner;

/**
 * <strong>Rol Adaptador de entrada por consola para ejecutar casos de uso</strong>
 *
 * <p>
 * Actuo como punto de arranque de la aplicacion y como interfaz por consola.
 * Presento un menu, recojo datos del usuario y orquesto la ejecucion de los servicios de aplicacion
 * {@link CrearClienteService} y {@link ConsultaSaldoService}.
 * </p>
 *
 * <p>
 * <strong>Proposito arquitectonico</strong><br>
 * Mantener la logica de interaccion con el usuario fuera del dominio.
 * Desde aqui se decide la implementacion del puerto {@link ClienteRepository}
 * ya sea OnPremise, Cloud, Mock o Composite para Dual Write.
 * </p>
 *
 * <strong>Nota</strong> La seleccion de repositorio se realiza en tiempo de ejecucion segun la opcion del menu.
 * En un escenario real esto suele resolverse por configuracion e inyeccion de dependencias.
 *
 * <p>
 * <strong>GitHub</strong> hwongu
 * </p>
 *
 * @author Henry Wong
 */
public class App {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion;
        System.out.println("=========================================");
        System.out.println("POC DUAL WRITER - INICIADA");
        System.out.println("=========================================");
        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opcion: ");
            // Aquí seleccionamos la estrategia (Simple o Composite)
            ClienteRepository repositorio = seleccionarRepositorio(opcion);
            if (repositorio != null) {
                // Lógica unificada: Procesamos inserción o consulta
                if (opcion == 7 || (opcion >= 1 && opcion <= 3)) {
                    procesarInsercion(repositorio);
                } else if (opcion >= 4 && opcion <= 6) {
                    procesarConsulta(repositorio);
                }
            } else if (opcion != 8) {
                System.out.println("Opción no válida.");
            }

        } while (opcion != 8);
        System.out.println("Aplicación finalizada.");
    }

    private static void mostrarMenu() {
        System.out.println("\n--- MENU OPCIONES ---");
        System.out.println("1. Insertar Cliente (Solo OnPremise)");
        System.out.println("2. Insertar Cliente (Solo Cloud)");
        System.out.println("3. Insertar Cliente (Mock)");
        System.out.println("4. Consultar Saldo (OnPremise)");
        System.out.println("5. Consultar Saldo (Cloud)");
        System.out.println("6. Consultar Saldo (Mock)");
        System.out.println("--------------------------------");
        System.out.println("7. Insertar DUAL WRITE (Composite: Legacy + Cloud)"); // <--- OPCIÓN COMPOSITE
        System.out.println("8. Salir");
    }

    private static ClienteRepository seleccionarRepositorio(int opcion) {
        switch (opcion) {
            case 1: case 4: return new ClienteRepositoryDbOnPremise();
            case 2: case 5: return new ClienteRepositoryDbCloud();
            case 3: case 6: return new ClienteRepositoryMock();

            case 7:
                // AQUÍ ARMAMOS EL COMPOSITE
                // Le pasamos las instancias de los repositorios que queremos agrupar
                return new CompositeClienteRepository(
                        new ClienteRepositoryDbCloud(),      // Prioridad 1 (Master)
                        new ClienteRepositoryDbOnPremise()   // Prioridad 2 (Legacy)
                );

            default: return null;
        }
    }



    private static void procesarInsercion(ClienteRepository repositorio) {
        System.out.println("\n--- NUEVO CLIENTE ---");
        String nombre = leerTexto("Ingrese nombre (Razón Social): ");
        Double saldo = leerDouble("Ingrese saldo inicial: ");
        CrearClienteService servicio = new CrearClienteService(repositorio);
        servicio.ejecutar(nombre, saldo);
    }

    private static void procesarConsulta(ClienteRepository repositorio) {
        System.out.println("\n--- CONSULTA DE SALDO ---");
        int id = leerEntero("Ingrese ID de Cliente: ");
        ConsultaSaldoService servicio = new ConsultaSaldoService(repositorio);
        servicio.ejecutarConsulta(id);
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine();
    }

    private static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        while (!sc.hasNextInt()) {
            System.out.println("Por favor, ingrese un número válido.");
            sc.next();
            System.out.print(mensaje);
        }
        int valor = sc.nextInt();
        sc.nextLine();
        return valor;
    }

    private static double leerDouble(String mensaje) {
        System.out.print(mensaje);
        while (!sc.hasNextDouble()) {
            System.out.println("Por favor, ingrese un monto válido (ej. 1500.50).");
            sc.next();
            System.out.print(mensaje);
        }
        double valor = sc.nextDouble();
        sc.nextLine();
        return valor;
    }
}