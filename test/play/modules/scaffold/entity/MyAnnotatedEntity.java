package play.modules.scaffold.entity;

import play.db.jpa.Model;
import play.modules.scaffold.Scaffolding;

@javax.persistence.Entity
@Scaffolding(controller="Foo")
public class MyAnnotatedEntity extends Model
{
	public MyEnum anEnum;
	public int anInt;
	public String aString;
	public boolean aPrimitiveBoolean;
	public Boolean aSlightlyMoreSophisticatedBoolean;
}
