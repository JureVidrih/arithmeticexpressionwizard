public class Examples {
    public static void main(String[] args) {
        ArithmeticExpressionWizard.specify("int i = (5+5);");
        ArithmeticExpressionWizard.specify("double [] myArray = new double [] {i, random.Next(5), floor(sqrt(7)), 5 };");
        System.out.println(ArithmeticExpressionWizard.solve("((myArray[0]*(5+5))+((2*2)/2))"));
    }
}