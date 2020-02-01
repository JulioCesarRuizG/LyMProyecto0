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
						if(partes[0].equals("assign:") && partes[2].equals("to:") && partes.length == 4)
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
								for(int i=0 ; i<arr.size() && esta == false ; i++)
								{
									if(arr.get(i).darNombre().equals(partes[3] ))
									{
										arr.get(i).aumentar(cantidad);
										esta = true;
										c = arr.get(i);
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
					else if(cadauno.startsWith("move: "))
					{
						int cantidad = 0;
						String[] partes = cadauno.split(" ");
						if(partes.length == 2)
						{
							if(partes[0].equals("move:"))
							{
								boolean esta = false;
								Iterator<Tupla> it = arr.iterator();
								for(int i=0 ; i<arr.size() && esta == false ; i++)
								{
									if(arr.get(i).darNombre().equals(partes[1]))
									{
										cantidad = arr.get(i).darCantidad();
										esta = true;
									}
								}
								if(esta == false)
								{
									try
									{
										cantidad = Integer.parseInt(partes[1]);
									} catch (Exception e) {
										throw new Exception("Se esperaba un número o variable luego de move:");
									}
								}
								world.moveForward(cantidad);
							}
						}
						else if(partes[2].equals("toThe") && partes.length == 4)
						{
							if(partes[0].equals("move:"))
							{
								boolean esta = false;
								Iterator<Tupla> it = arr.iterator();
								for(int i=0 ; i<arr.size() && esta == false ; i++)
								{
									if(arr.get(i).darNombre().equals(partes[1]))
									{
										cantidad = arr.get(i).darCantidad();
										esta = true;
									}
								}
								if(esta == false)
								{
									try
									{
										cantidad = Integer.parseInt(partes[1]);
									} catch (Exception e) {
										throw new Exception("Se esperaba un número o variable luego de move:");
									}
								}


								if(cantidad > 0)
								{
									if(partes[3].equals("front"))
									{
										world.moveForward(cantidad);
									}
									else if(partes[3].equals("right"))
									{
										world.turnRight();
										world.moveForward(cantidad);
										world.turnRight();
										world.turnRight();
										world.turnRight();
									}
									else if(partes[3].equals("left"))
									{
										world.turnRight();
										world.turnRight();
										world.turnRight();
										world.moveForward(cantidad);
										world.turnRight();
									}
									else if(partes[3].equals("back"))
									{
										world.turnRight();
										world.turnRight();
										world.moveForward(cantidad);
										world.turnRight();
										world.turnRight();
									}
									else {
										throw new Exception("se esperaba front, left, right o back: Invalid Entrance");
									}
								}
							}
						}
						else if(partes[2].equals("inDir")&& partes.length == 4)
						{
							if(partes[0].equals("move"))
							{
								boolean esta = false;
								Iterator<Tupla> it = arr.iterator();
								for(int i=0 ; i<arr.size() && esta == false ; i++)
								{
									if(arr.get(i).darNombre().equals(partes[1]))
									{
										cantidad = arr.get(i).darCantidad();
										esta = true;
									}
								}
								if(esta == false)
								{
									try
									{
										cantidad = Integer.parseInt(partes[1]);
									} catch (Exception e) {
										throw new Exception("Se esperaba un número o variable luego de move:");
									}
								}


								if(cantidad > 0)
								{
									if(partes[3].equals("north"))
									{
										if(world.facingEast())
										{
											world.turnRight();
											world.turnRight();
											world.turnRight();
											world.moveForward(cantidad);
										}
										else if(world.facingSouth())
										{
											world.turnRight();
											world.turnRight();
											world.moveForward(cantidad);
										}
										else if(world.facingWest())
										{
											world.turnRight();
											world.moveForward(cantidad);
										}
										else
										{
											world.moveForward(cantidad);
										}
									}
									else if(partes[3].equals("south"))
									{
										if(world.facingWest())
										{
											world.turnRight();
											world.turnRight();
											world.turnRight();
											world.moveForward(cantidad);
										}
										else if(world.facingNorth())
										{
											world.turnRight();
											world.turnRight();
											world.moveForward(cantidad);
										}
										else if(world.facingEast())
										{
											world.turnRight();
											world.moveForward(cantidad);
										}
										else
										{
											world.moveForward(cantidad);
										}
									}
									else if(partes[3].equals("east"))
									{
										if(world.facingSouth())
										{
											world.turnRight();
											world.turnRight();
											world.turnRight();
											world.moveForward(cantidad);
										}
										else if(world.facingWest())
										{
											world.turnRight();
											world.turnRight();
											world.moveForward(cantidad);
										}
										else if(world.facingNorth())
										{
											world.turnRight();
											world.moveForward(cantidad);
										}
										else
										{
											world.moveForward(cantidad);
										}
									}
									else if(partes[3].equals("west"))
									{
										if(world.facingNorth())
										{
											world.turnRight();
											world.turnRight();
											world.turnRight();
											world.moveForward(cantidad);
										}
										else if(world.facingEast())
										{
											world.turnRight();
											world.turnRight();
											world.moveForward(cantidad);
										}
										else if(world.facingSouth())
										{
											world.turnRight();
											world.moveForward(cantidad);
										}
										else
										{
											world.moveForward(cantidad);
										}
									}
								}
							}
						}
						else
						{
							throw new Exception("unknown move");
						}

					}
					else if(cadauno.startsWith("turn: "))
					{
						String[] partes = cadauno.split(" ");
						if(partes[0].equals("turn:") && partes.length == 2)
						{
							if(partes[1].equals("left"))
							{
								world.turnRight();
								world.turnRight();
								world.turnRight();
							}
							else if(partes[1].equals("right"))
							{
								world.turnRight();
							}
							else if(partes[1].equals("around"))
							{
								world.turnRight();
								world.turnRight();
							}
							else
							{
								throw new Exception("se esperaba left, right o around: Invalid Entrance");
							}
						}
					}
					else if(cadauno.startsWith("face: "))
					{
						String[] partes = cadauno.split(" ");
						if(partes[0].equals("face:") && partes.length == 2)
						{
							int orientacion = world.getOrientacion();
							if(partes[1].equals("north"))
							{
								if(orientacion == 1)
								{
									world.turnRight();
									world.turnRight();
								}
								else if(orientacion == 2)
								{
									world.turnRight();
									world.turnRight();
									world.turnRight();
								}
								else if(orientacion == 3)
								{
									world.turnRight();
								}
							}
							else if(partes[1].equals("south"))
							{

								if(orientacion == 0)
								{
									world.turnRight();
									world.turnRight();
								}
								else if(orientacion == 2)
								{
									world.turnRight();
								}
								else if(orientacion == 3)
								{
									world.turnRight();
									world.turnRight();
									world.turnRight();
								}
							}
							else if(partes[1].equals("east"))
							{
								if(orientacion == 0)
								{
									world.turnRight();
								}
								else if(orientacion == 1)
								{
									world.turnRight();
									world.turnRight();
									world.turnRight();
								}
								else if(orientacion == 3)
								{
									world.turnRight();
									world.turnRight();
								}
							}
							else if(partes[1].equals("west"))
							{
								if(orientacion == 0)
								{
									world.turnRight();
									world.turnRight();
									world.turnRight();
								}
								else if(orientacion == 1)
								{
									world.turnRight();

								}
								else if(orientacion == 2)
								{
									world.turnRight();
									world.turnRight();
								}
							}
							else
							{
								throw new Exception("se esperaba north, south, east or west: Invalid Entrance");
							}
						}
					}
					else if(cadauno.startsWith("put: "))
					{
						String[] partes = cadauno.split(" ");
						if(partes[0].equals("put:") && partes.length == 4)
						{
							int cantidad = 0;
							boolean esta = false;
							Iterator<Tupla> it = arr.iterator();
							for(int i=0 ; i<arr.size() && esta == false ; i++)
							{
								if(arr.get(i).darNombre().equals(partes[1]))
								{
									cantidad = arr.get(i).darCantidad();
									esta = true;
								}
							}
							if(esta == false)
							{
								try
								{
									cantidad = Integer.parseInt(partes[1]);
								} catch (Exception e) {
									throw new Exception("Se esperaba un número o variable luego de move:");
								}
							}


							if(cantidad > 0)
							{
								if(partes[3].equals("Balloons"))
								{
									if(world.getMisGlobos() < cantidad)
									{
										throw new Exception("No hay suficientes globos");
									}
									else
									{
										world.putBalloons(cantidad);
									}
								}
								else if(partes[3].equals("Chips"))
								{
									if(world.getMisGlobos() < cantidad)
									{
										throw new Exception("No hay suficientes chips");
									}
									else
									{
										world.putChips(cantidad);
									}
								}
								else
								{
									throw new Exception("se esperaba Balloons o Chips: Invalid Entrance");
								}
							}
						}
					}
					else if(cadauno.startsWith("pick: "))
					{
						String[] partes = cadauno.split(" ");
						if(partes[0].equals("pick:") && partes.length == 4)
						{
							int cantidad = 0;
							boolean esta = false;
							Iterator<Tupla> it = arr.iterator();
							for(int i=0 ; i<arr.size() && esta == false ; i++)
							{
								if(arr.get(i).darNombre().equals(partes[1]))
								{
									cantidad = arr.get(i).darCantidad();
									esta = true;
								}
							}
							if(esta == false)
							{
								try
								{
									cantidad = Integer.parseInt(partes[1]);
								} catch (Exception e) {
									throw new Exception("Se esperaba un número o variable luego de move:");
								}
							}


							if(cantidad > 0)
							{
								if(partes[3].equals("Balloons"))
								{
									world.grabBalloons(cantidad);
								}
								else if(partes[3].equals("Chips"))
								{
									world.pickChips(cantidad);
								}
								else
								{
									throw new Exception("se esperaba Balloons o Chips: Invalid Entrance");
								}
							}
						}
					}
					else if(cadauno.startsWith("Skip"))
					{
						String[] partes = cadauno.split(" ");
						if(partes.length == 1 && partes[0].equals("Skip"))
						{
							
						}
						else
						{
							throw new Exception("se esperaba Skip: Invalid Entrance");
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
