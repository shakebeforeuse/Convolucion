public class ConvolucionWorker implements Runnable
{
	private int inicio;
	private int fin;
	
	public ConvolucionWorker(int inicio, int fin)
	{
		this.inicio = inicio;
		this.fin    = fin;
	}
	
	public void run()
	{
		int despl = (int)Convolucion.kernel.length/2;
		
		for (int i = inicio; i < fin; ++i)
		{
			for (int j = 0; j < Convolucion.ancho; ++j)
			{
				for (int ki = 0; ki < Convolucion.kernel.length; ++ki)
					for (int kj = 0; kj < Convolucion.kernel.length; ++kj)
						Convolucion.salida[i][j] += Convolucion.entrada[mod(i + ki - despl, Convolucion.alto)][mod(j + kj - despl, Convolucion.ancho)] * Convolucion.kernel[ki][kj];
				
				if (Convolucion.salida[i][j] > 1)
					Convolucion.salida[i][j] = 1;
				else
					if (Convolucion.salida[i][j] < 0)
						Convolucion.salida[i][j] = 0;
			}
		}
	}
	
	private static int mod(int a, int b)
	{		
		return (a + b) % b;
	}
}