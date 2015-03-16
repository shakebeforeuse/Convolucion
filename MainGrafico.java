import java.util.Scanner;
import java.io.File;
import java.io.IOException;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;

public class MainGrafico
{
	private static Convolucion convolucion;
	private static BufferedImage imagen;
	private static double[][] entrada;
	
	private static JFrame frame;
	private static JPanel panel;
	private static JLabel picLabel;
	private static JTextField[][] kernelInput;
	private static JButton ejecutar;
	
	private static JFileChooser ficheroInput;
	private static File ficheroImagen;
	
	private static JComboBox selectorBox;
	private static String[] nombresKernel;
	
	private static SwingWorker worker;
	
	public static BufferedImage convertir(double[][] matriz)
	{
		BufferedImage imagen = new BufferedImage(matriz[0].length, matriz.length, BufferedImage.TYPE_4BYTE_ABGR);

		for(int i = 0 ; i < matriz[0].length ; ++i)
		{
			for(int j = 0 ; j < matriz.length ; ++j)
			{
				float r = (float)matriz[j][i];
				
				if (r < 0 || r > 1)
					System.out.println(i + " " + j + " " + r);
				
				Color c = new Color(r, r, r);
				imagen.setRGB(i, j, c.getRGB());
			}
		}

		return imagen;
	}
	
	public static double[][] cargar(File fichero) throws IOException
	{
		BufferedImage imagen = ImageIO.read(fichero);

		double[][] matriz = new double[imagen.getHeight()][imagen.getWidth()];

		for (int i = 0 ; i < imagen.getHeight() ; ++i)
		{
			for (int j = 0 ; j < imagen.getWidth() ; ++j)
			{
				Color c = new Color(imagen.getRGB(j, i));
				matriz[i][j] = ((c.getRed() + c.getGreen() + c.getBlue()) / 3.0) / 255;
			}
		}

		return matriz;
	}
	
	private static void GUI()
	{
		//Create and set up the window.
		frame = new JFrame("Convolución Paralela");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		panel = new JPanel();
		JPanel parametros = new JPanel();
		parametros.setLayout(new BoxLayout(parametros, BoxLayout.Y_AXIS));
		panel.add(parametros);
		
		JPanel kernelPanel = new JPanel(new GridLayout(3, 3));
		
		JLabel kernelText = new JLabel("Kernel");
		kernelText.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		kernelInput = new JTextField[3][3];
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 3; ++j)
				kernelInput[i][j] = new JTextField(String.valueOf(Convolucion.bordes[i][j]));
		
		parametros.add(kernelText);
		
		nombresKernel = new String[7];
		nombresKernel[0] = "Identidad";
		nombresKernel[1] = "Detección de bordes";
		nombresKernel[2] = "Detección de bordes (2)";
		nombresKernel[3] = "Detección de bordes (3)";
		nombresKernel[4] = "Box blur";
		nombresKernel[5] = "Sharpen";
		nombresKernel[6] = "Emboss";
		selectorBox = new JComboBox<String>(nombresKernel);
		selectorBox.setSelectedIndex(1);
		
		parametros.add(selectorBox);
		
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 3; ++j)
				kernelPanel.add(kernelInput[j][i]);
		
		parametros.add(kernelPanel);
		
		ejecutar = new JButton("Ejecutar");
		ejecutar.setAlignmentX(Component.CENTER_ALIGNMENT);
		parametros.add(ejecutar);
		
		ficheroInput = new JFileChooser();
		
		FileNameExtensionFilter filtro = new FileNameExtensionFilter("JPEG y PNG", "jpg", "png");
		ficheroInput.setFileFilter(filtro);
		
		
		frame.getContentPane().add(panel);

		frame.pack();
		frame.setVisible(true);
    }
	
	public static void main(String[] args) throws Exception
	{
		GUI();
		
		selectorBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				switch(selectorBox.getSelectedIndex())
				{
					case 0:
						//Identidad
						for (int i = 0; i < 3; ++i)
							for (int j = 0; j < 3; ++j)
								kernelInput[i][j].setText("0.0");
								
						kernelInput[1][1].setText("1.0");
					break;
					case 1:
						//Bordes
						for (int i = 0; i < 3; ++i)
							for (int j = 0; j < 3; ++j)
								kernelInput[i][j].setText("-1.0");
								
						kernelInput[1][1].setText("8.0");
					break;
					case 2:
						//Bordes 2
						kernelInput[0][0].setText("0.0");
						kernelInput[0][1].setText("1.0");
						kernelInput[0][2].setText("0.0");
						kernelInput[1][0].setText("1.0");
						kernelInput[1][1].setText("-4.0");
						kernelInput[1][2].setText("1.0");
						kernelInput[2][0].setText("0.0");
						kernelInput[2][1].setText("1.0");
						kernelInput[2][2].setText("0.0");
					break;
					case 3:
						//Bordes 3
						for (int i = 0; i < 3; ++i)
							for (int j = 0; j < 3; ++j)
								kernelInput[i][j].setText("0.0");
						
						kernelInput[0][0].setText("1.0");
						kernelInput[2][2].setText("1.0");
						kernelInput[0][2].setText("-1.0");
						kernelInput[2][2].setText("-1.0");
					break;
					case 4:
						//Box blur
						for (int i = 0; i < 3; ++i)
							for (int j = 0; j < 3; ++j)
								kernelInput[i][j].setText(String.valueOf(1 / 9.0));
					break;
					case 5:
						//Sharpen
						kernelInput[0][0].setText("0.0");
						kernelInput[0][1].setText("-1.0");
						kernelInput[0][2].setText("0.0");
						kernelInput[1][0].setText("-1.0");
						kernelInput[1][1].setText("5.0");
						kernelInput[1][2].setText("-1.0");
						kernelInput[2][0].setText("0.0");
						kernelInput[2][1].setText("-1.0");
						kernelInput[2][2].setText("0.0");
					break;
					case 6:
						//Sharpen
						kernelInput[0][0].setText("-2.0");
						kernelInput[0][1].setText("-1.0");
						kernelInput[0][2].setText("0.0");
						kernelInput[1][0].setText("-1.0");
						kernelInput[1][1].setText("1.0");
						kernelInput[1][2].setText("1.0");
						kernelInput[2][0].setText("0.0");
						kernelInput[2][1].setText("1.0");
						kernelInput[2][2].setText("2.0");
					break;
				}
			}
		});
		
		ejecutar.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (ficheroImagen == null)
				{
					if (ficheroInput.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION)
					{
						ficheroImagen = ficheroInput.getSelectedFile();
						
						try
						{
							entrada = cargar(ficheroImagen);
						}
						catch(IOException ex)
						{
							System.out.println("IOException: " + ex.getMessage());
						}
					}
				}
				
				if (ficheroImagen != null)
				{
					double[][] camposKernel  = new double[3][3];
					
					for (int i = 0; i < 3; ++i)
						for (int j = 0; j < 3; ++j)
							camposKernel[i][j] = Double.parseDouble(kernelInput[i][j].getText());
					
					convolucion = new Convolucion(entrada, Runtime.getRuntime().availableProcessors(), camposKernel);					
					
					worker = new SwingWorker<Void, Void>()
					{
						public Void doInBackground()
						{
							convolucion.ejecutar();
							imagen = convertir(convolucion.mostrar());
							
							return null;
						}
						
						public void done()
						{
							if (picLabel == null)
							{
								picLabel = new JLabel(new ImageIcon(imagen));
								panel.add(picLabel);
							}
							else
								picLabel.setIcon(new ImageIcon(imagen));
								
							panel.revalidate();
							panel.repaint();
							frame.pack();
						}
					};
					worker.execute();
				}
			}
		});
	}
}