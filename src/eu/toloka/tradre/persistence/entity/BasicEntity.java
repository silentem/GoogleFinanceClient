package eu.toloka.tradre.persistence.entity;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class BasicEntity {
    BasicEntity() {
    }

//    HashMap<String, Serializable> toMap() throws Exception {
//        HashMap<String, Serializable> map = new HashMap<String, Serializable>();
////        map.put("className", this.getClass().getName());
//
//        Field[] fields = this.getClass().getFields();
//
//        for (Field field : fields) {
//            Object value = field.get(this);
//
//            if (value instanceof Collection) {
//                @SuppressWarnings("unchecked")
//                ArrayList<HashMap<String, Serializable>> list = toMapList((Collection) value);
//                map.put(field.getName(), list);
//            } else if (value instanceof BasicEntity) {
//                map.put(field.getName(), ((BasicEntity) value).toMap());
//            } else {
//                if (value != null) {
//                    map.put(field.getName(), (Serializable) value);
//                }
//            }
//        }
//
//        return map;
//    }

//    private static ArrayList<HashMap<String, Serializable>> toMapList(Collection<? extends BasicEntity> list) throws Exception {
//        if (list == null) {
//            return null;
//        }
//
//        ArrayList<HashMap<String, Serializable>> maps = new ArrayList<HashMap<String, Serializable>>(list.size());
//
//        for (BasicEntity entity : list) {
//            maps.add(entity.toMap());
//        }
//
//        return maps;
//    }
}
