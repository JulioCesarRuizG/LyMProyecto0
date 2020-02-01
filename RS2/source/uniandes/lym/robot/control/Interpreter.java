package uniandes.lym.robot.control;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import uniandes.lym.robot.kernel.*;



/**
 * Receives commands and relays them to the Robot. 
 */

public class Interpreter   {

	public class Tupla
	{
		public String nombre;
		public int cantidad;

		public Tupla(String pNombre)
		{
			nombre = pNombre;
		}

		public String darNombre()
		{
			return nombre;
		}
		public int darCantidad()
		{
			return cantidad;
		}

		public void aumentar(int pCantidad)
		{
			cantidad = cantidad + pCantidad;
		}
	}

	/**
	 * Robot's world
	 */
	private RobotWorldDec world;   


	public Interpreter()
	{
	}


	/**
	 * Creates a new interpreter for a given world
	 * @param world 
	 */


	public Interpreter(RobotWorld mundo)
	{
		this.world =  (RobotWorldDec) mundo;

	}


	/**
	 * sets a the world
	 * @param world 
	 */

	public void setWorld(RobotWorld m) 
	{
		world = (RobotWorldDec) m;

	}



	/**
	 *  Processes a sequence of commands. A command is a letter  followed by a ";"
	 *  The command can be:
	 *  M:  moves forward
	 *  R:  turns right
	 *  
	 * @param input Contiene una cadena de texto enviada para ser interpretada
	 * @throws Exception 
	 */

	public String process(String input) throws Error, Exception
	{   
		Tupla c = null;
		ArrayList<Tupla> arr = new ArrayList<Interpreter.Tupla>();
		if(input.startsWith("ROBOT_R"))
		{
			String parte2 = input.substring(7);
			if(parte2.startsWith("VARS"))
			{
				String sep1 = parte2.substring(4);
				String[] nombres = sep1.split(",");
				for(String s:nombres)
				{
					Tupla t = new Tupla(s);
					arr.add(t);
				}
			}
			else if(parte2.startsWith("BEGIN"))
			{
				String sep1 = parte2.substring(5);
				String[] comandos = sep1.split(";");
				for(String cadauno : comandos)
				{
					if(cadauno.endsWith("END"))
					{
						String ultimo = cadauno.substring(0, cadauno.length()-3);
						cadauno = ultimo;
					}
					if(cadauno.startsWith("assign:"))
					{
						String[] partes = cadauno.split(" ");
						if(partes[0].equals("assign:") && partes[2].equals("to:"))
						{
							int cantidad = 0;
							try
							{
								cantidad = Integer.parseInt(partes[1]);
							} catch (Exception e) {
								throw new Exception("Se esperaba un número luego de assign:");
							}

							if(cantidad != 0)
							{
								
								boolean esta = false;
								Iterator<Tupla> it = arr.iterator();
								while(it.hasNext() && esta == false)
								{
									it.next();
									Tupla t = (Tupla) it;
									System.out.println(t.darNombre());
									if(t.darNombre().equals(partes[3]))
									{
										c=t;
										t.aumentar(cantidad);
										esta = true;
									}
								}
								if(esta == true)
								{
									System.out.println(c.darCantidad() + "");
								}
								else
								{
									throw new Exception("unknown var");
								}
							}
							else
							{

								throw new Exception("unknown type");
							}
						}
						else
						{
							throw new Exception("unknown block");
						}
					}
					else
					{
						System.out.println(cadauno);
						throw new Exception("unknown block");
					}
				}
			}
			else
			{
				throw new Exception("unknown command");
			}
		}

		StringBuffer output=new StringBuffer("SYSTEM RESPONSE: -->\n");	

		int i;
		int n;
		boolean ok = true;
		n= input.length();

		i  = 0;
		try	    {
			while (i < n &&  ok) {
				switch (input.charAt(i)) {
				case 'M': world.moveForward(1); output.append("move \n");break;
				case 'R': world.turnRight(); output.append("turnRignt \n");break;
				case 'C': world.putChips(1); output.append("putChip \n");break;
				case 'B': world.putBalloons(1); output.append("putBalloon \n");break;
				case  'c': world.pickChips(1); output.append("getChip \n");break;
				case  'b': world.grabBalloons(1); output.append("getBalloon \n");break;
				default: output.append(" Unrecognized command:  "+ input.charAt(i)); ok=false;
				}

				if (ok) {
					if  (i+1 == n)  { output.append("expected ';' ; found end of input; ");  ok = false ;}
					else if (input.charAt(i+1) == ';') 
					{
						i= i+2;
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							System.err.format("IOException: %s%n", e);
						}

					}
					else {output.append(" Expecting ;  found: "+ input.charAt(i+1)); ok=false;
					}
				}


			}

		}
		catch (Error e ){
			output.append("Error!!!  "+e.getMessage());

		}
		return output.toString();
	}
}
