public class Main {

    public static void main(String[] args) {
        AttrValue e = new DeterministicValue(1.0);
        AttrValue f = new DeterministicValue(3.0);
       // AttributeValue g = new DiscrDstrValue([2 0.5 3 0.5]);
        System.out.println("Hello World");
        System.out.println(AttrValFunctional.substract(e,f));
    }
}
