package play.modules.scaffold.utils;

import java.util.Collection;
import java.util.Iterator;

public abstract class Filterable<T>
{
	public static <T> void remove(Collection<T> c, Filterable<T> closure)
	{
		Iterator<T> it = c.iterator();
		while (it.hasNext())
		{
			if (closure.filter(it.next()))
				it.remove();
		}
	}
	
	public abstract boolean filter(T input);
}
