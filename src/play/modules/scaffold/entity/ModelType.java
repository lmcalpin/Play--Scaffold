package play.modules.scaffold.entity;

import play.modules.scaffold.strategy.JpaViewScaffoldingStrategy;
import play.modules.scaffold.strategy.SienaViewScaffoldingStrategy;
import play.modules.scaffold.strategy.ViewScaffoldingStrategy;

public enum ModelType
{
	PLAY_JPA,
	SIENA;
	
	public ViewScaffoldingStrategy getViewScaffoldingStrategy() 
	{
		switch(this)
		{
		case PLAY_JPA: return new JpaViewScaffoldingStrategy();
		case SIENA: return new SienaViewScaffoldingStrategy();
		}
		return null;
	}
}
