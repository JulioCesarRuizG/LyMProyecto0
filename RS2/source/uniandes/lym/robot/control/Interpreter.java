package uniandes.lym.robot.control;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

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

	public String process(String input) 
	{   
		StringBuffer output=new StringBuffer("SYSTEM RESPONSE: -->\n");	

		try
		{
			ArrayList<Tupla> arr = new ArrayList<Interpreter.Tupla>();
			if(input.startsWith("ROBOT_R")&&input.contains("BEGIN")&&input.endsWith("END"))
			{
				String parte2 = input.substring(7);
				String[] cadenaFinal = parte2.split("BEGIN");
				String[] comandos;

				if( parte2.contains("VARS") )
				{
					String sep1 = cadenaFinal[0].substring(4);
					String[] nombres = sep1.split(",");

					for(String s:nombres)
					{
						Tupla t = new Tupla(s);
						arr.add(t);
					}
					 comandos = cadenaFinal[1].split(";");
				}
				else
				{
					 comandos = cadenaFinal[1].split(";");
				}

				metodoComandos(comandos, arr);

			}
			else
			{
				throw new Exception("unknown command");
			}
		}
		catch( Exception e )
		{
			output.append( e.getMessage( ) );
		}
		return output.toString( );


		/** StringBuffer output=new StringBuffer("SYSTEM RESPONSE: -->\n");	

		int i;
		int n;
		boolean ok = true;
		n= input.length();

		i  = 0;
		try	    
		{
			while (i < n &&  ok) 
			{
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
			output.append("Error!!!  "+e.getMessage());}

		return output.toString();
		 */

	}

	/**
	 * Metodo para evaluar los condicionales
	 * @throws Exception 
	 */


	public boolean evaluarFacing(String direccion) throws Exception
	{
		if(direccion.equals("north")) 
		{
			if(world.facingNorth())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else if(direccion.equals("south"))
		{
			if(world.facingSouth())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else if(direccion.equals("east"))
		{
			if(world.facingEast())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else if(direccion.equals("west"))
		{
			if(world.facingWest())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			throw new Exception("Unknown condition");
		}
	}

	public boolean evaluarCanPut(String n, String x, ArrayList<Tupla> arreglo) throws Exception
	{
		int cantidad = 0;
		boolean esta = false;
		for(int i=0 ; i<arreglo.size() && esta == false ; i++)
		{
			if(arreglo.get(i).darNombre().equals(n))
			{
				cantidad = arreglo.get(i).darCantidad();
				esta = true;
			}
		}
		if(esta == false)
		{
			try
			{
				cantidad = Integer.parseInt(n);
			} catch (Exception e) {
				throw new Exception("Se esperaba un número o variable luego de canPut:");
			}
		}
		if(cantidad > 0)
		{
			if(x.equals("balloons"))
			{
				try
				{
					world.putBalloons(cantidad);
					return true;
				}
				catch (Exception e) {
					return false;
				}
			}
			else if(x.equals("chips"))
			{
				try
				{
					world.putChips(cantidad);
					return true;
				}
				catch (Exception e) {
					return false;
				}
			}
			else
			{
				throw new Exception("Invalid entrance");
			}
		}
		else
		{
			throw new Exception("Invalid entrance");
		}
	}

	public boolean evaluarCanPick(String n, String x, ArrayList<Tupla> arreglo) throws Exception
	{
		int cantidad = 0;
		boolean esta = false;
		for(int i=0 ; i<arreglo.size() && esta == false ; i++)
		{
			if(arreglo.get(i).darNombre().equals(n))
			{
				cantidad = arreglo.get(i).darCantidad();
				esta = true;
			}
		}
		if(esta == false)
		{
			try
			{
				cantidad = Integer.parseInt(n);
			} catch (Exception e) {
				throw new Exception("Se esperaba un número o variable luego de canPut:");
			}
		}
		if(cantidad > 0)
		{
			if(x.equals("balloons"))
			{
				try
				{
					world.grabBalloons(cantidad);
					return true;
				}
				catch (Exception e) {
					return false;
				}
			}
			else if(x.equals("chips"))
			{
				try
				{
					world.pickChips(cantidad);
					return true;
				}
				catch (Exception e) {
					return false;
				}
			}
			else
			{
				throw new Exception("Invalid entrance");
			}
		}
		else
		{
			throw new Exception("Invalid entrance");
		}
	}

	public boolean evaluarCanMove(String direccion) throws Exception
	{
		if(direccion.equals("north"))
		{
			if(world.getPosicion().getY() == 0)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else if(direccion.equals("south"))
		{
			if(world.getPosicion().getY() == 7)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else if(direccion.equals("west"))
		{
			if(world.getPosicion().getX() == 0)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else if(direccion.equals("east"))
		{
			if(world.getPosicion().getX() == 7)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else
		{
			throw new Exception("Invalid entrance");
		}
	}

	/**
	 * metodo que ejecuta los comandos ingresados por el usuarios
	 */
	public void metodoComandos( String[] comandos, ArrayList<Tupla> arreglo) throws Error, Exception
	{
		Tupla c = null;
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
						for(int i=0 ; i<arreglo.size() && esta == false ; i++)
						{
							if(arreglo.get(i).darNombre().equals(partes[3] ))
							{
								arreglo.get(i).aumentar(cantidad);
								esta = true;
								c = arreglo.get(i);
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
						Iterator<Tupla> it = arreglo.iterator();
						for(int i=0 ; i<arreglo.size() && esta == false ; i++)
						{
							if(arreglo.get(i).darNombre().equals(partes[1]))
							{
								cantidad = arreglo.get(i).darCantidad();
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
				else if(partes[2].equals("toThe:") && partes.length == 4)
				{
					if(partes[0].equals("move:"))
					{
						boolean esta = false;
						Iterator<Tupla> it = arreglo.iterator();
						for(int i=0 ; i<arreglo.size() && esta == false ; i++)
						{
							if(arreglo.get(i).darNombre().equals(partes[1]))
							{
								cantidad = arreglo.get(i).darCantidad();
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
				else if(partes[2].equals("inDir:")&& partes.length == 4)
				{
					if(partes[0].equals("move:"))
					{
						boolean esta = false;
						Iterator<Tupla> it = arreglo.iterator();
						for(int i=0 ; i<arreglo.size() && esta == false ; i++)
						{
							if(arreglo.get(i).darNombre().equals(partes[1]))
							{
								cantidad = arreglo.get(i).darCantidad();
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
					Iterator<Tupla> it = arreglo.iterator();
					for(int i=0 ; i<arreglo.size() && esta == false ; i++)
					{
						if(arreglo.get(i).darNombre().equals(partes[1]))
						{
							cantidad = arreglo.get(i).darCantidad();
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
					Iterator<Tupla> it = arreglo.iterator();
					for(int i=0 ; i<arreglo.size() && esta == false ; i++)
					{
						if(arreglo.get(i).darNombre().equals(partes[1]))
						{
							cantidad = arreglo.get(i).darCantidad();
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
			else if(cadauno.startsWith("if: ") && cadauno.contains(" then: ") && cadauno.contains(" else: "))
			{
				try
				{
					String partes = cadauno.substring(4);
					String[] parte2 = partes.split(" then: ");
					String[] parte3 = parte2[1].split(" else: ");
					String cond = parte2[0];
					String bloque1 = parte3[0];
					String bloque2 = parte3[1];
					String[] bloquefinal1 = bloque1.split(";");
					String[] bloquefinal2 = bloque2.split(";");
					if(evaluarCondicional(true, cond, arreglo))
					{
						metodoComandos(bloquefinal1, arreglo);
					}
					else
					{
						metodoComandos(bloquefinal2, arreglo);
					}
				}
				catch (Exception e) {
					throw new Exception(e.getMessage());
				}

			}
			else if(cadauno.startsWith("while: ") && cadauno.contains(" do: "))
			{
				try
				{
					String partes = cadauno.substring(7);
					String[] parte2 = partes.split(" do: ");
					String cond = parte2[0];
					String bloque = parte2[1];
					String[] bloquefinal = bloque.split(";");
					while(evaluarCondicional(true, cond, arreglo))
					{
						metodoComandos(bloquefinal, arreglo);
					}
				}
				catch (Exception e) {
					e.getMessage();
				}	
			}
			else if(cadauno.startsWith("repeat: ") && cadauno.contains(" times: "))
			{
				try
				{
					String partes = cadauno.substring(8);
					String[] parte2 = partes.split(" times: ");
					String bloque = parte2[0];
					String veces = parte2[1];
					String[] bloquefinal = bloque.split(";");

					int cantidad = 0;
					boolean esta = false;
					Iterator<Tupla> it = arreglo.iterator();
					for(int i=0 ; i<arreglo.size() && esta == false ; i++)
					{
						if(arreglo.get(i).darNombre().equals(veces))
						{
							cantidad = arreglo.get(i).darCantidad();
							esta = true;
						}
					}
					if(esta == false)
					{
						try
						{
							cantidad = Integer.parseInt(veces);
						} catch (Exception e) {
							throw new Exception("Se esperaba un número o variable luego de move:");
						}
					}
					if(cantidad > 0)
					{
						while(cantidad != 0)
						{
							metodoComandos(bloquefinal, arreglo);
							cantidad--;
						}
					}
					else
					{
						throw new Exception("Variable o número inválido");
					}
				}
				catch (Exception e) {
					e.getMessage();
				}
			}


		}
	}


	public boolean evaluarCondicional( boolean a, String condition, ArrayList<Tupla> arreglo ) throws Exception
	{
		String[] partes = condition.split(" ");
		boolean resultado = false;

		if( partes[0].equals("facing:"))
		{
			resultado = evaluarFacing( partes[1] );
		}
		else if( partes[0].equals("canPut:") )
		{
			resultado = evaluarCanPut( partes[1], partes[3], arreglo );
		}
		else if( partes[0].equals("canMove:") )
		{
			resultado = evaluarCanMove( partes[1] );
		}
		else if( partes[0].equals("canPick:") )
		{
			resultado = evaluarCanPick( partes[1], partes[3], arreglo );
		}
		else if( partes[0].equals("not:") )
		{
			resultado = a && !evaluarCondicional( a, partes[1], arreglo);
		}
		else
		{
			throw new Exception("La condicion ingresada no es valida");
		}

		return resultado;

	}
}

