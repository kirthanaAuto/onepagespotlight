package com.aeione.ops.generic;

import org.testng.*;
import org.testng.annotations.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import static com.aeione.ops.generic.IAutoConst.*;

public class AnnotationTransformerListener implements IAnnotationTransformer {
    public GoogleSheetAPI sheetAPI() throws IOException
    {
        return new GoogleSheetAPI();
    }

    public AnnotationTransformerListener()
    {

    }

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        String currentTestCaseName = testMethod.getName();
        try {
            ArrayList<String> totalSheets= sheetAPI().getSheetsName(TEST_EXECUTION_SHEET);

            String overViewSheet=totalSheets.get(0);
            String range = ""+totalSheets.get(1)+"!A1:ZZ";

            Map<String, String> values = sheetAPI().getSpreadSheetRowValueByColumnValue(TEST_EXECUTION_SHEET, range, currentTestCaseName);

            String testModule = values.get("Module").trim();
            String testCaseName = values.get("TestCaseName").trim();
            String priority = values.get("Priority").trim();
            String enabled = values.get("Enabled").trim();

            System.out.println(testModule+ "," +testCaseName + "," + priority + "," + enabled);


            //Get overview of Modules
            String overViewRange = ""+overViewSheet+"!A1:ZZ";

            Map<String, String> overViewValues = sheetAPI().getSpreadSheetRowValueByColumnValue(TEST_EXECUTION_SHEET, overViewRange,testModule);

            String module = overViewValues.get("Module").trim();
            String executionMode = overViewValues.get("Execution Mode").trim();
            String suiteType= overViewValues.get("Suite Type").trim();

            System.out.println(module + "," + executionMode + "," + suiteType);

            switch(executionMode)
            {
                case Enabled:
                    annotation.setEnabled(true);
                    break;

                case Disabled:
                    annotation.setEnabled(false);
                    break;

                case Enabled_Specific_Test_Cases:
                    if(Boolean.valueOf(enabled.toLowerCase())==true && !enabled.trim().equals(""))
                    {
                        annotation.setEnabled(true);
                    }
                    else
                    {
                        annotation.setEnabled(false);
                    }
                    break;

                case Disabled_Specific_Test_Cases:
                    if(Boolean.valueOf(enabled.toLowerCase())==false && !enabled.trim().equals(""))
                    {
                        annotation.setEnabled(false);
                    }
                    else
                    {
                        annotation.setEnabled(true);
                    }
                    break;

            }
            annotation.setPriority(Integer.valueOf(priority));



        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void TestCaseLevelModification()
    {

    }

}
