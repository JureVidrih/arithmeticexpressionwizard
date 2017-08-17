import java.util.ArrayList;
import java.util.HashMap;

/**
 * This ArithmeticExpressionWizard class can process arithmetical expressions with either decimal or floating-point numbers and variables.
 * The expression's result can then be outputted to a String or stored in a separate variable. The stored variables can later be used
 * in other arithmetical expressions. Additionally, this class also provides support for declaring variables and arrays.
 *
 * The support for mathematical functions, such as floor, random and sqrt is also present.
 */
public class ArithmeticExpressionWizard {

    // These are the maps that are used to store the variables.

    public static HashMap<String, Double> doubleList;
    public static HashMap<String, Double[]> doubleArrayList;
    public static HashMap<String, Integer> intList;
    public static HashMap<String, Integer[]> intArrayList;

    // The declaration of main fields of this class.

    private static String input;
    private static ArrayList<String> numbers;
    private static ArrayList<String> operators;
    private static String varName;
    private static int varArrayIndex;
    private static boolean isOrigin;
    private static boolean isDefinition;
    private static boolean isInteger;
    private static boolean isArray;

    // The static initialization for the maps and some other fields.

    static {
        doubleList = new HashMap<>();
        doubleArrayList = new HashMap<>();
        intList = new HashMap<>();
        intArrayList = new HashMap<>();

        input = "";
        numbers = new ArrayList<>();
        operators = new ArrayList<>();
        isOrigin = false;
        isDefinition = false;
        isInteger = true;
        isArray = false;
        varName = "";
        varArrayIndex = 0;
    }

    // The standard constructor. It just initializes the fields that are not static and need an initial value.

    private ArithmeticExpressionWizard() {

    }

    /**
     * This function iterates over the inputString String (the arithmetical expression passed into the {@link process(String inputString)} function)
     * and removes all the whitespaces from it. This allows for a more standardized approach to processing the expression, without
     * having to worry about the different amount of whitespaces before and after the inputString String's tokens.
     */
    public static String triminputString(String inputString) {
        String output = "";
        for(int i = 0; i < inputString.length(); i++) {
            if(inputString.charAt(i) != ' ') {
                output += inputString.charAt(i);
            }
        }

        return output;
    }

    public static String process(String inputString) {

        System.out.println("NEW PROCESS: " + inputString);

        // Check, if the inputString is a declaration, definition or initialization of a variable or an array.

        if(specify(inputString)) {
            return "No further action necessary. Aborting.";
        }

        /* TOKEN SCANNING AND PROCESSING */

        String result = solve(input);

        // Output the result

        return result;
    }

    public static String solve(String inputString) {

        System.out.println("SOLVING: " + inputString);

        // Trim the inputString if necessary.

        if(inputString.contains(" ")) {
            input = triminputString(inputString);
        } else {
            input = inputString;
        }

        // Scan the expression for tokens to process. If the expression is invalid, an exception will be thrown.

        ScannedExp scannedExp = scan();

        numbers = scannedExp.getNumbersList();
        operators = scannedExp.getOperatorsList();

        // Initializing some variables needed for processing.

        boolean isTherePrecedence = false;
        double result = 0;
        double tempResult = 0.0;


        // In this loop, we check for the '^' operators and perform the power operations where needed.

        for(int i = 0; i < operators.size(); i++) {
            if(operators.get(i).equals("^")) {
                numbers.set(i, Double.toString(Math.pow(Double.parseDouble(numbers.get(i)), Double.parseDouble(numbers.get(i + 1)))));
                operators.remove(i);
                numbers.remove(i+1);
                if(operators.size() > 0) {
                    i--;
                }
            }
        }

        // If the numbers list contains at least one number, then the returning result variable's value is set to that value.
        // This is done in case if the inputString contains just one number or a variable, so that the program returns something.
        // Also, we check to see if there aren't any numbers in the inputString at all. In that case, we terminate the program.

        if (numbers.size() > 0) {
            result = Double.parseDouble(numbers.get(0));
        } else {
            throw new IllegalArgumentException("The inputString String contains no valid numbers or variables.");
        }

        // The main loop for the process operation. This is where the final result is calculated.
        // The loop runs until the iterator variable i becomes as big as the size of the operator list.
        // We use the operators list instead of the numbers list here, because when we perform operations on
        // numbers and variables, we always need two numbers and because the size of the operator list
        // is always going to be smaller than that of the numbers list by one element, we use it here. So, in the
        // last iteration cycle, we will be using a number(i) with an operator(i) and another number(i+1). Therefore,
        // if we used the numbers list for the boolean expression, we would be accessing an element in the numbers list
        // that doesn't exist.

        for(int i = 0; i < operators.size(); i++) {

            // This is where we check, if there is an multiplicaiton or division operation ahead, so that we then perform it first.
            // Also, we check if there is '^' operator ahead. If so, we perform a power operation on the next number.

            System.out.println("i: " + i + ", size: " + numbers.size());
            if (i < (numbers.size() - 2)) {
                if ((operators.get(i + 1).equals("*") || operators.get(i + 1).equals("/")) && !operators.get(i).equals("*") && !operators.get(i).equals("/")) {
                    isTherePrecedence = true;
                }
            }

            // Here is where we actually perform the calculations. If there is an operator ahead with precedence,
            // we perform it first, but that is the case only for multiplication and division.

            if (isTherePrecedence) {
                switch (operators.get(i + 1)) {
                    case "*":
                        tempResult = Double.parseDouble(numbers.get(i + 1)) * Double.parseDouble(numbers.get(i + 2));
                           break;
                    case "/":
                        tempResult = Double.parseDouble(numbers.get(i + 1)) / Double.parseDouble(numbers.get(i + 2));
                        break;
                }
                operators.remove(i + 1);
                numbers.remove(i + 2);
                numbers.set(i + 1, Double.toString(tempResult));
                i--;
            } else {
                switch (operators.get(i)) {
                    case "+":
                        result += Double.parseDouble(numbers.get(i + 1));
                        break;
                    case "-":
                        result -= Double.parseDouble(numbers.get(i + 1));
                        break;
                    case "*":
                        result *= Double.parseDouble(numbers.get(i + 1));
                        break;
                    case "/":
                        result /= Double.parseDouble(numbers.get(i + 1));
                        break;
                }
            }
            isTherePrecedence = false;
        }

        scannedExp.reset();

        if((result % 1.0) != 0.0) {
            isInteger = false;
        } else {
            isInteger = true;
        }

        if (isInteger) {
            System.out.println("SOLUTION IS: " + (int)result);
            return Integer.toString((int) result);
        } else {
            System.out.println("SOLUTION IS: " + result);
            return Double.toString(result);
        }
    }

    /**
     * This method declares, defines or initializes the variablee or array in the inputString.
     * @param inputString The inputString String to process.
     * @return If a declaration, definition or initialization has been performed, the returned value will be true. False, otherwise.
     */
    public static boolean specify(String inputString) {

        // Reset some boolean variables

        isOrigin = false;
        isDefinition = false;
        isInteger = true;
        isArray = false;
        varName = "";
        varArrayIndex = 0;

        // Just a quick check to see if the inputString String contains any whitespaces.
        // If it does, then it is trimmed. Otherwise, no action is performed.

        if(inputString.contains(" ")) {
            input = triminputString(inputString);
        } else {
            input = inputString;
        }

        if(input.charAt(input.length()-1) != ';') {
            input += ';';
        }

        // If the function contains the '=' character, then it is either a variable/array
        // initialization or a variable/array[index] definition. Thus, it is called an origin
        // or a definition.

        int cursor = 0;

        if(input.startsWith("int")) {
            isOrigin = true;
            cursor = 3;
        } else if(input.startsWith("double")) {
            isOrigin = true;
            isInteger = false;
            cursor = 6;
        } else {
            if(input.contains("=")) {
                isDefinition = true;
            }
        }

        if(isOrigin) {
            input = input.substring(cursor, input.length());
            if(input.contains("=")) {
                if(input.substring(0, input.indexOf("=")).contains("[")) {
                    isArray = true;
                    varName = input.substring(2, input.indexOf("="));
                } else {
                    varName = input.substring(0, input.indexOf("="));
                }
                input = input.substring(input.indexOf('=')+1, input.length());
                if(!isArray) {
                    String saveInput = input;
                    double result = Double.parseDouble(solve(input));
                    input = saveInput;
                    if (isInteger) {
                        addVariable(varName, (int)result);
                        System.out.println("Adding a variable " + varName + " with a value of " + (int)result);
                    } else {
                        addVariable(varName, result);
                        System.out.println("Adding a variable " + varName + " with a value of " + result);
                    }
                    return true;
                } else {
                    if(input.contains("{")) {
                        input = input.substring(input.indexOf('{')+1, input.indexOf('}')+1);
                        int arraySize = 1;
                        String tempString = "";
                        for(int i = 0; i < input.length(); i++) {
                            if(input.charAt(i) == ',') {
                                arraySize += 1;
                            }
                            if(input.charAt(i) == ';') {
                                StringBuilder sb = new StringBuilder(input);
                                sb.deleteCharAt(i);
                                input = sb.toString();
                                i--;
                            }
                        }
                        if(isInteger) {
                            Integer[] array = new Integer[arraySize];
                            for(int i = 0, index = 0; i < input.length(); i++) {
                                if(input.charAt(i) == ',' || input.charAt(i) == '}') {
                                    String saveInput = input;
                                    tempString = ArithmeticExpressionWizard.solve(tempString);
                                    input = saveInput;
                                    array[index] = Integer.parseInt(tempString);
                                    tempString = "";
                                    index++;
                                } else {
                                    tempString += input.charAt(i);
                                }
                            }
                            addVariable(varName, array);
                        } else if(!isInteger) {
                            Double[] array = new Double[arraySize];
                            tempString = "";
                            for(int i = 0, index = 0; i < input.length(); i++) {
                                if(input.charAt(i) == ',' || input.charAt(i) == '}') {
                                    String saveInput = input;
                                    tempString = ArithmeticExpressionWizard.solve(tempString);
                                    input = saveInput;
                                    isInteger = false;
                                    array[index] = Double.parseDouble(tempString);
                                    tempString = "";
                                    index++;
                                } else {
                                    tempString += input.charAt(i);
                                }
                            }
                            addVariable(varName, array);
                        }
                        System.out.println("Array " + varName + " added.");
                        return true;
                    } else {
                        int arraySize = Integer.parseInt(input.substring(input.indexOf('[')+1, input.indexOf(']')));
                        if(isInteger) {
                            addVariable(varName, new Integer[arraySize]);
                            System.out.println("Empty integer array " + varName + " added.");
                            return true;
                        } else {
                            addVariable(varName, new Double[arraySize]);
                            System.out.println("Empty double array " + varName + " added.");
                            return true;
                        }
                    }
                }
            } else {
                varName = input.substring(0, input.indexOf(";"));
                addVariable(varName, 0);
                System.out.println("Variable " + varName + " added.");
                return true;
            }
        }

        if(isDefinition) {
            String literalVarName = input.substring(0, input.indexOf('='));
            if(literalVarName.matches("[A-z]") || literalVarName.matches("[A-z]+[A-z0-9]") || literalVarName.contains("[")) {
                isDefinition = true;
                if((input.substring(0, input.indexOf('='))).contains("[")) {
                    isArray = true;
                    varName = input.substring(0, input.indexOf('['));
                    varArrayIndex = Integer.parseInt(input.substring(input.indexOf('[')+1, input.indexOf(']')));
                } else {
                    varName = input.substring(0, input.indexOf('='));
                }
                input = input.substring(input.indexOf('=')+1, input.length());
                String saveInput = input;
                double result = Double.parseDouble(solve(input));
                input = saveInput;
                if(isArray) {
                    if (isInteger) {
                        setVariable(varName, varArrayIndex, (int)result);
                        System.out.println("Setting the variable " + varName + "["+varArrayIndex+"]" + " with the value of " + (int)result);
                    } else {
                        setVariable(varName, varArrayIndex, result);
                        System.out.println("Setting the variable " + varName + "["+varArrayIndex+"]" + " with the value of " + result);
                    }
                }
                if(!isArray) {
                    if (isInteger) {
                        setVariable(varName, (int)result);
                        System.out.println("Setting the variable " + varName + " with the value of " + (int)result);
                    } else {
                        setVariable(varName, result);
                        System.out.println("Setting the variable " + varName + " with the value of " + result);
                    }
                }
                return true;
            }
        }

        return false;
    }

    /**
     * This method scans for tokens in the inputString string and adds them to either the numbers list or the operators list.
     * If it find a variable, it will change it into its number value and handle it like a number token.
     * @return This method returns a ScannedExp object, which is basically a wrapper for two ArrayLists for numbers and operators.
     */
    public static ScannedExp scan() {

        // Initialize the object this method will return.

        ScannedExp retObj = new ScannedExp();

        // Just initializing some variables needed for the scan process.

        String tempString = "";
        char tempChar = ' ';

        boolean inParentheses = false;
        int depth = 0;
        int cursorStart = 0;
        int cursorEnd = 0;

        if(input.charAt(input.length()-1) != ';') {
            input += ";";
        }

        // This is the main loop for scanning. It iterates through each character in the inputString String and then processes it.

        for(int i = 0; i < input.length(); i++) {

            // Here we access the character that we are going to process from the inputString String.

            tempChar = input.charAt(i);

            // If the character is a '(', then we tell the program that what follows is a nested expression. We also record the position of the said character
            // in the inputString String for later use.

            if(tempChar == '(') {
                inParentheses = true;
                if(depth == 0) {
                    cursorStart = i;
                }
                depth += 1;
            } /* if the character is a ')', then we check, if it represent the end of the actual nested expression. We use the variable "depth" for that purpose.
            If the depth variable indicates the end of the nested expression, we then perform a recursive call to this class to process it. */
            else if(tempChar == ')') {
                depth -= 1;
                if(depth == 0) {
                    cursorEnd = i;
                    String saveInput = input;
                    tempString = ArithmeticExpressionWizard.solve(input.substring(cursorStart+1, cursorEnd));
                    input = saveInput;
                    inParentheses = false;

                    // Since there is no point in further processing the ')' character, as we have done it already, we iterate
                    // over to the next cycle of the for loop.

                    continue;
                }
            }

            // If the character being read is not nested in an expression, we then process it. If the character is not an operator, then
            // we add the character to the variable "tempString". If the character is an operator, we then add the operator to the
            // operator list and the tempString to the numbers list. After that, we set tempString to an empty value. If the tempString
            // is a variable, we then first transform it into its number value and then add it to the list. But, if the tempString is
            // an actual mathematical function, then we process it and its arguments instead and turn it into a number value and then
            // add it to the numbers list.

            if(!inParentheses) {
                if(tempChar == '+' || tempChar == '-' || tempChar == '*' || tempChar == '/' || tempChar == '^' || tempChar == ';') {
                    if(tempChar != ';') {
                        retObj.addToOperatorsList(String.valueOf(tempChar));
                    }
                    if(tempString.equals("")) {
                        throw new IllegalArgumentException("No valid number to the left of the operator!");
                    } else {
                        if(tempString.matches("[0-9]") || tempString.matches("[0-9]+[0-9]") || tempString.matches("-[0-9]") || tempString.matches("-[0-9]+[0-9]") || tempString.contains(".")) {
                            retObj.addToNumbersList(tempString);
                        } else if(tempString.matches("[A-z]") || tempString.matches("[A-z]+[A-z0-9]") || tempString.contains("[")) {
                            int arrayIndex = 0;
                            Object[] varObj = null;
                            if(tempString.contains("[")) {
                                arrayIndex = Integer.parseInt(tempString.substring(tempString.indexOf('[')+1, tempString.indexOf(']')));
                                varObj = getVariable(tempString.substring(0, tempString.indexOf('[')));
                            } else {
                                varObj = getVariable(tempString.substring(0, tempString.length()));
                            }
                            if(varObj.length == 1) {
                                if(varObj instanceof Integer[]) {
                                    retObj.addToNumbersList(Integer.toString((Integer)varObj[0]));
                                } else if(varObj instanceof Double[]) {
                                    retObj.addToNumbersList(Double.toString((Double)varObj[0]));
                                    isInteger = false;
                                }
                            } else if(varObj.length > 1) {
                                if(varObj instanceof Integer[]) {
                                    retObj.addToNumbersList(Integer.toString((Integer)varObj[arrayIndex]));
                                } else if(varObj instanceof Double[]) {
                                    retObj.addToNumbersList(""+(Double)varObj[arrayIndex]);
                                    isInteger = false;
                                }
                            }
                        }
                        tempString = "";
                    }
                } else {
                    tempString += tempChar;
                    if(tempString.startsWith("random") || tempString.startsWith("floor") || tempString.startsWith("sqrt")) {
                        String tempValueString = "";
                        int tempDepth = 0;
                        int tempIndex = 0;
                        for(int startIndex = input.indexOf('(', i), iterator = startIndex; iterator < input.length(); iterator++) {
                            if(input.charAt(iterator) == '(') {
                                tempDepth++;
                            } else if(input.charAt(iterator) == ')') {
                                tempDepth--;
                            }
                            if(tempDepth == 0) {
                                tempIndex = (iterator += 1) - startIndex;
                                break;
                            }
                        }
                        tempString += input.substring(input.indexOf('(', i), (tempIndex)+input.indexOf('(', i));
                        tempValueString = tempString.substring(tempString.indexOf('(')+1, (tempString.indexOf('(', i)+tempIndex-1));
                        String saveInput = input;
                        tempValueString = ArithmeticExpressionWizard.solve(tempValueString);
                        input = saveInput;

                        if(tempString.startsWith("random")) {
                            if(tempValueString.contains(".")) {
                                double range = Double.parseDouble(tempValueString);
                                tempString = Double.toString((double)(Math.random()*range+0));
                            } else {
                                int range = Integer.parseInt(tempValueString);
                                tempString = Integer.toString((int)(Math.random()*range+0));
                            }
                        } else if(tempString.startsWith("floor")) {
                            double varToFloor = Double.parseDouble(tempValueString);
                            tempString = Double.toString((double)(Math.floor(varToFloor)));
                        } else if(tempString.startsWith("sqrt")) {
                            double varToSqrt = Double.parseDouble(tempValueString);
                            tempString = Double.toString((double)(Math.sqrt(varToSqrt)));
                            if(Double.parseDouble(tempString) % 1.0 == 0.0) {
                                tempString = tempString.substring(0, tempString.indexOf('.'));
                            }
                            isInteger = false;
                        }
                        i = (input.indexOf('(', i)+tempIndex)-1;

                        continue;
                    }
                }
            }
        }
        
        return retObj;
    }

    /**
     * This function performs a check on the variable/array lists.
     * @param name The variable name to check.
     * @return A boolean value of true, if the lists already contain a variable with the said name or false otherwise.
     */
    public static boolean checkList(String name) {
        if (doubleList.containsKey(name) || doubleArrayList.containsKey(name) || intList.containsKey(name) || intArrayList.containsKey(name)) {
            System.out.println("One of the lists (already) contains an object with a name: " + name);
            return true;
        }

        System.out.println("There is no variable called " + name + " in any of the lists.");
        return false;
    }

    /**
     * This function adds the variable to the list, but only, if a variable with the same name doesn't already exist in the list.
     * @param name The variable's name.
     * @param value The variable's value.
     */
    public static void addVariable(String name, Double value) {
        if (!checkList(name)) {
            doubleList.putIfAbsent(name, value);
        }
    }

    /**
     * This function adds the variable to the list, but only, if a variable with the same name doesn't already exist in the list.
     * @param name The variable's name.
     * @param value The variable's value.
     */
    public static void addVariable(String name, Double[] value) {
        if (!checkList(name)) {
            doubleArrayList.put(name, value);
        }
    }

     /**
     * This function adds the variable to the list, but only, if a variable with the same name doesn't already exist in the list.
     * @param name The variable's name.
     * @param value The variable's value.
     */
    public static void addVariable(String name, Integer value) {
        if (!checkList(name)) {
            intList.put(name, value);
        }
    }

    /**
     * This function adds the variable to the list, but only, if a variable with the same name doesn't already exist in the list.
     * @param name The variable's name.
     * @param value The variable's value.
     */
    public static void addVariable(String name, Integer[] value) {
        if (!checkList(name)) {
            intArrayList.put(name, value);
        }
    }

    /**
     * This function sets the value of the specified variable in the list, but only, if a variable with the same name does already exist in the list.
     * @param name The variable's name.
     * @param value The variable's value.
     */
    public static void setVariable(String name, Integer value) {
        if (checkList(name)) {
            intList.replace(name, value);
        }
    }

    /**
     * This function sets the value of the specified variable in the list, but only, if a variable with the same name does already exist in the list.
     * @param name The variable's name.
     * @param value The variable's value.
     */
    public static void setVariable(String name, int index, Integer value) {
        if (checkList(name)) {
            Integer[] tempArray = intArrayList.get(name);
            tempArray[index] = value;
            intArrayList.replace(name, tempArray);
        }
    }

    /**
     * This function sets the value of the specified variable in the list, but only, if a variable with the same name does already exist in the list.
     * @param name The variable's name.
     * @param value The variable's value.
     */
    public static void setVariable(String name, Double value) {
        if (checkList(name)) {
            doubleList.replace(name, value);
        }
    }
    /**
     * This function sets the value of the specified variable in the list, but only, if a variable with the same name does already exist in the list.
     * @param name The variable's name.
     * @param value The variable's value.
     */
    public static void setVariable(String name, int index, Double value) {
        if (checkList(name)) {
            Double[] tempArray = doubleArrayList.get(name);
            tempArray[index] = value;
            doubleArrayList.replace(name, tempArray);
        }
    }

    /**
     * This function checks the list for the specified variable and then returns it.
     * @param name The name of the variable to return.
     * @return This method returns an Object variable as an array. If the variable found in one of the list
     * is a variable and not an array, then the returned array will have a length of 1.
     */
    public static Object[] getVariable(String name) {
        System.out.println("Getting a variable " + name + " from the list.");

        Object[] returnedVariable = null;

        if(!checkList(name)) {
            System.out.println("There is no variable called " + name + " in any of the lists.");
        } else {
            if (doubleList.containsKey(name)) {
                returnedVariable = new Double[] { doubleList.get(name) };
            } else if (doubleArrayList.containsKey(name)) {
                returnedVariable = doubleArrayList.get(name);
            } else if (intList.containsKey(name)) {
                returnedVariable = new Integer[] { intList.get(name) };
            } else if (intArrayList.containsKey(name)) {
                returnedVariable = intArrayList.get(name);
            }
        }

        System.out.println("Returning " + name + " with a length of " + returnedVariable.length);;

        return returnedVariable;
    }

    /**
     * ScannedExp is just a wrapper class for two ArrayLists that are returned by the scan() function and are wrapped in this class.
     */
    private static class ScannedExp {
        private ArrayList<String> numbers;
        private ArrayList<String> operators;

        public ScannedExp() {
            numbers = new ArrayList<>();
            operators = new ArrayList<>();
        }
        
        public void reset() {
            if(numbers != null && operators != null) {
                numbers.clear();
                operators.clear();
            }
        }

        public ArrayList<String> getNumbersList() {
            return numbers;
        }

        public ArrayList<String> getOperatorsList() {
            return operators;
        }

        public void addToNumbersList(String item) {
            System.out.println("Adding item " + item + " to the numbers list.");
            this.numbers.add(item);
        }

        public void addToOperatorsList(String item) {
            System.out.println("Adding item " + item + " to the operators list.");
            this.operators.add(item);
        }
    }
}