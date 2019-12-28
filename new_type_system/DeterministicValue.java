import java.util.Collections;
import java.util.Iterator;

public class DeterministicValue extends AttrValue {
    private Double value;

    public DeterministicValue(Double a){
        this.value = a;
    }
    public Double getValue(){
        return value;
    }

    public Double getProb(){
        return 1.0;
    }

    @Override
    public Iterator iterator() {
        return Collections.singleton(value).iterator();
    }

    @Override
    public boolean equals(Object o){
        return this.value == ((DeterministicValue)o).value;
    }

    @Override
    public Object apply(Object o) {
        if(o.equals(this)) return 1.0;
        return 0.0;
    }

    @Override
    public String toString() {
        return "value="+value+" probability="+"1.0";
    }

}