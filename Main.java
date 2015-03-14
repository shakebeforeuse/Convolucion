import java.util.Scanner;

public class Main
{
	private static int tam, tareas;
	
	private static double tic, tac;
	
	public static void main(String[] args) throws Exception
	{
		if (args.length >= 1)
		{
			tam         = Integer.parseInt(args[0]);
			tareas      = 0;
		}
		else
		{
			if (args.length == 2)
				tareas = Integer.parseInt(args[1]);
			else
			{
				Scanner teclado = new Scanner(System.in);
				
				System.out.println("Uso: java Main <Tamaño> [<Tareas>]");
				
				System.out.println("Introduce un tamaño válido");
				tam = teclado.nextInt();
				System.out.println("Introduce el número de tareas que se usarán");
				tareas = teclado.nextInt();
				
				teclado.close();
			}
		}
		
		if (tareas < 1)
			tareas = Runtime.getRuntime().availableProcessors();

		Convolucion convolucion = new Convolucion(tam, tam, tareas);

		
		tic = System.currentTimeMillis();
		convolucion.ejecutar();
		tac = System.currentTimeMillis();
		
		
		System.out.println("Tiempo: " + (tac-tic) + " ms.");
		convolucion.close();
	}
}