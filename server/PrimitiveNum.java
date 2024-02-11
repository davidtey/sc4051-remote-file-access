package server;

enum PrimitiveNum {
    INT,
    STRING;

    public static PrimitiveNum fromInt(int x){
        switch(x){
            case 1:
                return INT;
            case 2:
                return STRING;
        }
        return null;
    }
}