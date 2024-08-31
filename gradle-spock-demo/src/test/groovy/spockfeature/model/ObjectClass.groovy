package spockfeature.model

class ObjectClass {
    private int val
    private String name

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    int getVal() {
        return val
    }

    void setVal(int val) {
        this.val = val
    }

    ObjectClass(int val, String name) {
        this.val = val
        this.name = name
    }
}
