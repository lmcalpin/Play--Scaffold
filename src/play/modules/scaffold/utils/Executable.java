package play.modules.scaffold.utils;

import java.util.Collection;
import java.util.Iterator;

public abstract class Executable<T>
{
	public static <T> void each(Collection<T> c, Executable<T> block)
	{
		Iterator<T> it = c.iterator();
		while (it.hasNext())
		{
			block.execute(it.next());
		}
	}
	
	public abstract void execute(T input);
}
