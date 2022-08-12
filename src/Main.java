import org.deidentifier.arx.*;
import org.deidentifier.arx.criteria.KAnonymity;

import java.io.IOException;
import java.nio.charset.Charset;
import java.io.File;
import java.util.jar.Attributes;

public class Main {

    public static void main(String[] args) throws Exception {
        Data data = Data.create("C:\\Users\\Jan\\Downloads\\mlb_players.csv", Charset.defaultCharset(), ',');
        String[] Attributes = {"Name", "Team", "Position", "Height(inches)", "Weight(lbs)", "Age"};

        // classify attributes
        data.getDefinition().setAttributeType("Name",AttributeType.IDENTIFYING_ATTRIBUTE);
        data.getDefinition().setAttributeType("Team",AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
        data.getDefinition().setAttributeType("Position",AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
        data.getDefinition().setAttributeType("Height(inches)",AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
        data.getDefinition().setAttributeType("Weight(lbs)",AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
        data.getDefinition().setAttributeType("Age",AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);

        // load hierarchy
        File age = new File("arx_age.csv");
        AttributeType.Hierarchy ageHierarchy = AttributeType.Hierarchy.create(age, Charset.defaultCharset(), ',');
        File height = new File("arx_height.csv");
        AttributeType.Hierarchy heightHierarchy = AttributeType.Hierarchy.create(height, Charset.defaultCharset(), ',');
        File position = new File("arx_position.csv");
        AttributeType.Hierarchy positionHierarchy = AttributeType.Hierarchy.create(position, Charset.defaultCharset(), ',');
        File team = new File("arx_team.csv");
        AttributeType.Hierarchy teamHierarchy = AttributeType.Hierarchy.create(team, Charset.defaultCharset(), ',');
        File weight = new File("arx_weight.csv");
        AttributeType.Hierarchy weightHierarchy = AttributeType.Hierarchy.create(weight, Charset.defaultCharset(), ',');
        data.getDefinition().setAttributeType("Age", ageHierarchy);
        data.getDefinition().setAttributeType("Height(inches)", heightHierarchy);
        data.getDefinition().setAttributeType("Position", positionHierarchy);
        data.getDefinition().setAttributeType("Team", teamHierarchy);
        data.getDefinition().setAttributeType("Weight(lbs)", weightHierarchy);

        //create config
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(4));
        config.setSuppressionLimit(0);

        ARXAnonymizer anonymizer = new ARXAnonymizer();
        anonymizer.setMaximumSnapshotSizeDataset(0.2);
        anonymizer.setMaximumSnapshotSizeSnapshot(0.2);
        anonymizer.setHistorySize(200);


        long startTime = System.nanoTime();
        ARXResult result = anonymizer.anonymize(data, config);
        long stopTime = System.nanoTime();
        System.out.println(stopTime - startTime);
        System.out.println(result.getTime());

        // anders als in Dokumentation
        ARXLattice.ARXNode node = result.getGlobalOptimum();
        // auch anders
        DataHandle handle = result.getOutput();
        handle.save("result.csv", ',');
        printGeneralization(handle, Attributes);
    }

    public static void printGeneralization(DataHandle handle, String[] Attributes){
        System.out.print("[");
        for( String Attribute : Attributes){
            System.out.print(handle.getGeneralization(Attribute) + ",");
        }
        System.out.println("]");
    }

    public static void printGeneralization(ARXLattice.ARXNode node, String[] Attributes){
        System.out.print("[");
        for( String Attribute : Attributes){
            System.out.print(node.getGeneralization(Attribute) + ",");
        }
        System.out.println("]");
    }
}
