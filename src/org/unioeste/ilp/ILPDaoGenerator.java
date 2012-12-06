/*
 * "Copyright 2012 Lucas André de Alencar"
 * 
 * This file is part of ILPModelsGenerator.
 * 
 * ILPModelsGenerator is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * ILPModelsGenerator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License 
 * along with ILPModelsGenerator.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.unioeste.ilp;

import java.io.IOException;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

/**
 * Class used on the generation of schema and DAOs
 * used on the Android app.
 * 
 * GreenDAO {http://greendao-orm.com/} is added as an ORM.
 * It is responsible for the classes generation and the
 * Android DB management.
 * 
 * @author Lucas André de Alencar
 *
 */
public class ILPDaoGenerator {
	
	static Entity user;
	static Entity pattern;
	static Entity experience;
	static Entity attempt;
	static Entity sample;
	
	// Used on DB versioning
	private static final int DB_VERSION = 1;
	
	public static void main(String[] args) throws IOException, Exception {
		// Initializes schema setting the models package
		Schema schema = new Schema(DB_VERSION, "org.unioeste.ilp.models");
		
		// Creates the entities on the schema
		user = addUser(schema);
		pattern = addPattern(schema);
		experience = addExperience(schema);
		attempt = addAttempt(schema);
		sample = addSample(schema);
		
		// Makes the relations between the entities
		buildRelations();
		
		// Generates the schema and exports the classes on the specified directory
		new DaoGenerator().generateAll(schema, "../IntelligentLockPattern/src");
	}
	
	private static Entity addUser (Schema schema) {
		Entity user = schema.addEntity("User");
		user.setTableName("users");
		user.addIdProperty();
		user.addStringProperty("name").notNull().unique();
		return user;
	}
	
	private static Entity addPattern (Schema schema) {
		Entity pattern = schema.addEntity("Pattern");
		pattern.setTableName("patterns");
		pattern.addIdProperty();
		pattern.addStringProperty("pattern_sha1").notNull().unique();
		pattern.addStringProperty("pattern_string").notNull();
		return pattern;
	}
	
	private static Entity addExperience (Schema schema) {
		Entity experience = schema.addEntity("Experience");
		experience.setTableName("experiences");
		experience.addIdProperty();
		experience.addBooleanProperty("done");
		return experience;
	}
	
	private static Entity addAttempt (Schema schema) {
		Entity attempt = schema.addEntity("Attempt");
		attempt.setTableName("attempts");
		attempt.addIdProperty();
		return attempt;
	}
	
	private static Entity addSample (Schema schema) {
		Entity sample = schema.addEntity("Sample");
		sample.setTableName("samples");
		sample.addIdProperty();
		sample.addDoubleProperty("event_time").notNull();
		sample.addDoubleProperty("pressure").notNull();
		sample.addDoubleProperty("pressure_area").notNull();
		return sample;
	}
	
	/**
	 * Makes the one to many relation with specified entities.
	 * 
	 * @param one Entity with one relation
	 * @param many Entity with many relation
	 * @param manyPlural Entity's name on plural used on table's name
	 */
	private static void oneToMany (Entity one, Entity many, String manyPlural) {
		Property oneIdProperty = many.addLongProperty(formatIdAttr(one.getClassName())).getProperty();
		many.addToOne(one, oneIdProperty);
		ToMany oneToManyAttr = one.addToMany(many, oneIdProperty);
		oneToManyAttr.setName(manyPlural);
	}
	
	private static String formatIdAttr (String className) {
		String attr = className.toLowerCase();
		return attr.concat("_id");
	}
	
	private static void buildRelations () {
		oneToMany(user, experience, "experiences");
		oneToMany(pattern, experience, "experiences");
		oneToMany(experience, attempt, "attempts");
		oneToMany(attempt, sample, "samples");
	}
}
