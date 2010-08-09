package play.modules.scaffold.entity;

import javax.persistence.Id;

import play.db.jpa.Model;

@javax.persistence.Entity
public class MyEntity extends Model {
	public MyEnum anEnum;
	public int anInt;
	public String aString;
	public boolean aPrimitiveBoolean;
	public Boolean aSlightlyMoreSophisticatedBoolean;
	@Id
	public String theId;
}
