
public abstract class AttrValFunctional {

    public static AttrValue
    substract(AttrValue x, AttrValue y) {
        if(x instanceof DeterministicValue && y instanceof DeterministicValue){
            return subtractDD((DeterministicValue)x,(DeterministicValue)y);
        }
        if(x instanceof DeterministicValue && y instanceof DiscreteDistributionValue){
            return subtractDDD((DeterministicValue)x,(DiscreteDistributionValue)y);
        }
        if(x instanceof DiscreteDistributionValue && y instanceof DeterministicValue){
            return negative(subtractDDD((DeterministicValue)y,(DiscreteDistributionValue)x));
        }
        if(x instanceof DiscreteDistributionValue && y instanceof DiscreteDistributionValue){
            return subtractDDDD((DiscreteDistributionValue)y,(DiscreteDistributionValue)x);
        }
        return null;
    }

    private static DeterministicValue negative(DeterministicValue a){
        return new DeterministicValue(-a.getValue());
    }

    private static DiscreteDistributionValue negative(DiscreteDistributionValue b){
        return new DiscreteDistributionValue();
    }

    private static DeterministicValue subtractDD(DeterministicValue a, DeterministicValue b){
        return new DeterministicValue(a.getValue() - b.getValue());
    }

    private static DiscreteDistributionValue subtractDDD(DeterministicValue a, DiscreteDistributionValue b){
        return new DiscreteDistributionValue();
    }

    private static DiscreteDistributionValue subtractDDDD(DiscreteDistributionValue a, DiscreteDistributionValue b){
        return new DiscreteDistributionValue();
    }

}
