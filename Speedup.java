import java.util.Scanner;

public class Speedup
{
	private static int tam, maxTareas;
	private static double tic, tac;
	private static double[] tiempos;
	
	public static void main(String[] args) throws Exception
	{
		if (args.length == 2)
		{
			tam = Integer.parseInt(args[0]);
			maxTareas = Integer.parseInt(args[1]);
		}
		else
		{
			Scanner teclado = new Scanner(System.in);
			System.out.println("Introduce un tamaño válido");
			tam = teclado.nextInt();
			System.out.println("Introduce el máximo de tareas a ejecutar");
			maxTareas = teclado.nextInt();
			teclado.close();
		}
		
		tiempos = new double[maxTareas];
		
		Convolucion convolucion = new Convolucion(tam, tam);
		
		System.out.println("Tareas\tSpeedup\tTiempo");
		
		for (int n = 1; n <= maxTareas; ++n)
		{
			convolucion.close();
			convolucion.nucleos(n);
			
			tic = System.currentTimeMillis();
			
			for (int i = 0; i < 100; ++i)
				convolucion.ejecutar();
				
			tac = System.currentTimeMillis();
			
			tiempos[n - 1] = (tac - tic) / 100;
			
			System.out.println(n + "\t" + (tiempos[0] / tiempos[n-1]) + "\t" + tiempos[n-1]);
		}
		
		convolucion.close();
	}
}