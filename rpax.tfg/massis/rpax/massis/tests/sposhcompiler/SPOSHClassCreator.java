package rpax.massis.tests.sposhcompiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import rpax.massis.ia.sposh.SPOSHLogicController;
import cz.cuni.amis.pogamut.sposh.elements.DriveElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;

public class SPOSHClassCreator {

	public static void main(String[] args) {
		final String POSH_PLAN_PATH = "src/main/resources/sposh/plan/iespalomeras/profesor.lap";

		PoshPlan plan = SPOSHLogicController
				.loadPoshPlanFromFile(POSH_PLAN_PATH);
		try (Writer w=new BufferedWriter(new FileWriter(new File("cajonbasura/TEST.java"))))
		{
			writeClass(w,plan)		;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void writeClass(Writer writer, PoshPlan plan) throws IOException {
		//Import
		writeImports(writer,plan);
		//Header
		writeHeader(writer,plan);
		//Variables
		writeVariables(writer,plan);
		writeMethods(writer,plan);
		closeClass(writer,plan);
		
		
	}

	private static void writeMethods(Writer writer, PoshPlan plan) throws IOException {
		for (DriveElement drive : plan.getDriveCollection().getDrives())
		{
			
			writer.write("void execute_"+drive.getName().replace("-", "_")+"() ");

			writer.write(DataFlavors.processAction(plan, drive.getAction()).transform(1));

		}
	}

	private static void closeClass(Writer writer, PoshPlan plan) throws IOException {
		writer.write("\n}");
	}

	private static void writeVariables(Writer writer, PoshPlan plan) throws IOException {
		for (String action : plan.getActionsNames())
		{
			writer.write(action);
			writer.write(" ");
			writer.write(action.replace(".", "_"));
			writer.write(";");
			writer.write("\n");
		}
	}

	private static void writeImports(Writer writer, PoshPlan plan) throws IOException {
		writer.write("import rpax.tfg.*;\n");
	}

	private static void writeHeader(Writer writer, PoshPlan plan) throws IOException {
		writer.write("public final class TEST {\n");
	}
	
}
