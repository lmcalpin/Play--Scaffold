package play.modules.scaffold.entity;

import play.db.jpa.Model;

@javax.persistence.Entity
public class MyEntity extends Model {
	public MyEnum anEnum;
	public int anInt;
	public String aString;
	public boolean aPrimitiveBoolean;
	public Boolean aSlightlyMoreSophisticatedBoolean;
}
