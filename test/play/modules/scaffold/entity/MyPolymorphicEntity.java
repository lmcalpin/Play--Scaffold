package play.modules.scaffold.entity;

import javax.persistence.Lob;

@javax.persistence.Entity
public class MyPolymorphicEntity extends MyEntity
{
	@Lob
	public String aLongString;
}
