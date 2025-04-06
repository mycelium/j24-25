package lab1.CatSON;

public class CatSON {

    public static String toJson(Object obj){
        if (obj == null){
            return "null";
        }

        if (obj instanceof Double){
            if(((Double) obj).isNaN() || ((Double) obj).isInfinite()){
                throw new IllegalArgumentException("Nan or Infinite value can`t be read to JSON");
            }
            System.out.println("nt pr");
            return obj.toString();
        }

        if (obj instanceof Float){
            if(((Float) obj).isNaN() || ((Float) obj).isInfinite()){
                throw new IllegalArgumentException("Nan or Infinite value can`t be read to JSON");
            }
            return obj.toString();
        }


        return "";
    }

}
