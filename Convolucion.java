import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.Random;
import java.util.Scanner;

public class Convolucion implements Runnable
{	
	public static int ancho;
	public static int alto;
	public static double[][] entrada;
	public static double[][] salida;
	public static double[][] kernel;
	
	public final static double[][] identidad = {{0, 0, 0}, {0, 1, 0}, {0, 0, 0}};
	public final static double[][] bordes    = {{-1, -1, -1}, {-1, 8, -1}, {-1, -1, -1}};
	
	private Random random;
	
	private static int nucleos;
	private static ExecutorService threadPool;
	private static Runnable[] tareas;
	private static Future<?>[] futuros;
	
	private int inicio;
	private int fin;	
	
	public Convolucion(double[][] imagen, int nTareas, double[][] kernel)
	{
		ancho       = imagen[0].length;
		alto        = imagen.length;
		entrada     = imagen;
		salida      = new double[alto][ancho];
		this.kernel = kernel;
		
		nucleos(nTareas);
		
		random = new Random();
	}
	
	public Convolucion(int ancho, int alto, int nTareas, double[][] kernel)
	{
		this.ancho  = ancho;
		this.alto   = alto;
		entrada     = new double[alto][ancho];
		salida      = new double[alto][ancho];
		this.kernel = kernel;
		
		nucleos(nTareas);
		
		random = new Random();
		aleatorio();
	}
	
	public Convolucion(int ancho, int alto, int nTareas)
	{		
		this(ancho, alto, nTareas, identidad);
	}
	
	public Convolucion(int ancho, int alto)
	{
		this(ancho, alto, Runtime.getRuntime().availableProcessors());
	}
	
	private Convolucion(int inicio, int fin, boolean dummy)
	{
		this.inicio = inicio;
		this.fin    = fin;
	}
	
	public void aleatorio()
	{	
		for (int i = 0; i < alto; ++i)
			for (int j = 0; j < ancho; ++j)
				entrada[i][j] = random.nextDouble();
	}
	
	public void nucleos(int n)
	{
		if (threadPool != null)
			this.close();
		
		nucleos    = n;
		threadPool = Executors.newFixedThreadPool(nucleos);
		tareas     = new Runnable[nucleos];
		futuros    = new Future<?>[nucleos];
		
		for (int i = 0; i < nucleos; ++i)
		{
			int inicioIntervalo = i * (alto / nucleos);
			int finIntervalo    = (i+1) * (alto/nucleos);
			
			if ((i+1) == nucleos)
				finIntervalo = alto;
			
			tareas[i] = new Convolucion(inicioIntervalo, finIntervalo, false);
		}
	}
	
	public static void ejecutar()
	{
		for (int i = 0; i < tareas.length; ++i)
			futuros[i] = threadPool.submit(tareas[i]);
		
		//Espera a que terminen las tareas, para poder medir bien el tiempo en el hilo principal
		try
		{
			for (int i = 0; i < futuros.length; ++i)
				futuros[i].get();
		}
		catch (ExecutionException e)
		{
			System.out.println("ExecutionException: Convolucion.ejecutar(): " + e.getMessage());
		}
		catch (InterruptedException e)
		{
			System.out.println("InterruptedException: Convolucion.ejecutar(): " + e.getMessage());
		}
	}
	
	public static double[][] mostrar()
	{
		return salida;
	}
	
	public void close()
	{		
		while (!threadPool.isTerminated())
			threadPool.shutdown();
	}
	
	public void run()
	{
		int despl = (int)kernel.length/2;
		
		for (int i = inicio; i < fin; ++i)
		{
			for (int j = 0; j < ancho; ++j)
			{
				for (int ki = 0; ki < kernel.length; ++ki)
					for (int kj = 0; kj < kernel.length; ++kj)
						salida[i][j] += entrada[mod(i + ki - despl, alto)][mod(j + kj - despl, ancho)] * kernel[ki][kj];
				
				if (salida[i][j] > 1)
					salida[i][j] = 1;
				else
					if (salida[i][j] < 0)
						salida[i][j] = 0;
			}
		}
	}
	
	private static int mod(int a, int b)
	{		
		return (a + b) % b;
	}
	
	public static void main(String[] args)
	{
		int tam    = 0;
		int it     = 0;
		int tareas = 0;
		
		double tic, tac;
		
		if (args.length >= 2)
		{
			tam         = Integer.parseInt(args[0]);
			it          = Integer.parseInt(args[1]);
		}
		else
		{
			if (args.length == 3)
				tareas = Integer.parseInt(args[1]);
			else
			{
				Scanner teclado = new Scanner(System.in);
				
				System.out.println("Uso: java Convolucion <Tamaño> <Iteraciones> [<Tareas>]");
				
				System.out.println("Introduce un tamaño válido");
				tam = teclado.nextInt();
				System.out.println("Introduce un número de iteraciones");
				it = teclado.nextInt();
				System.out.println("Introduce el número de tareas que se usarán");
				tareas = teclado.nextInt();
				
				teclado.close();
			}
		}
		
		if (tareas < 1)
			tareas = Runtime.getRuntime().availableProcessors();

		Convolucion convolucion = new Convolucion(tam, tam, tareas);

		
		tic = System.currentTimeMillis();
		for (int i = 0; i < it; ++i)
			convolucion.ejecutar();
		tac = System.currentTimeMillis();
		
		
		System.out.println("Tiempo: " + ((tac - tic) / it) + " ms.");
		convolucion.close();
	}
}
