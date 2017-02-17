package eu.toloka.tradre.persistence.dao;

import eu.toloka.tradre.persistence.PersistenceManager;
import eu.toloka.tradre.persistence.entity.MovingEntity;

import java.util.List;

import static eu.toloka.tradre.persistence.PersistenceManager.getEntityManager;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class MovingDao {

    private static final int[] intervals = {60, 120, 300, 600, 900, 3600, 3600 * 24};
    private static final int[] movingArr= {13, 21, 34, 55, 89, 144, 200};

    private static final Boolean[][][] matrix = MovingDao.getMatrix();

    private static MovingEntity getMoving(Integer chartInterval, Integer interval) {
        String query = "SELECT OBJECT(entity) FROM MovingEntity entity WHERE entity.chartInter = " + chartInterval +
                " AND entity.inter = " + interval;

        @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
        List<MovingEntity> list = (List<MovingEntity>) getEntityManager().createQuery(query).getResultList();

        return list.size() == 0 ? null : list.get(0);
    }

    private static List<MovingEntity> getMovingList(Integer chartInterval) {
        String query = "SELECT OBJECT(entity) FROM MovingEntity entity WHERE entity.chartInter = " + chartInterval + " ORDER BY entity.inter";

        @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
        List<MovingEntity> list = (List<MovingEntity>) getEntityManager().createQuery(query).getResultList();

        return list;
    }

    public static Integer getIntervalIndex(int interval) {
        for(int i = 0; i < intervals.length; i++){
            if(intervals[i] == interval){
                return i;
            }
        }

        return null;
    }

    public static Integer getMovingIndex(int moving) {

        for(int i = 0; i < movingArr.length; i++){
            if(movingArr[i] == moving){
                return i;
            }
        }

        return null;
    }

    public static Boolean[][] getMatrix(int interval) {
        return matrix[getIntervalIndex(interval)];
    }

    private static Boolean[][][] getMatrix() {
        Boolean[][][] movingMatrix = new Boolean[7][7][7];//chart interval, interval, moving
        // 60, 120, 300, 600, 900, 3600, 3600 * 24
        // m13, m21, m34, m55, m89, m144, m200

        for(int i = 0; i < intervals.length; i++ ){
            List<MovingEntity> list = getMovingList(intervals[i]);

            for(int j = 0; j < intervals.length; j++ ){
                for(MovingEntity movingEntity: list){
                    if(movingEntity.inter.equals(intervals[j])){
                        movingMatrix[i][j][0] = movingEntity.m13;
                        movingMatrix[i][j][1] = movingEntity.m21;
                        movingMatrix[i][j][2] = movingEntity.m34;
                        movingMatrix[i][j][3] = movingEntity.m55;
                        movingMatrix[i][j][4] = movingEntity.m89;
                        movingMatrix[i][j][5] = movingEntity.m144;
                        movingMatrix[i][j][6] = movingEntity.m200;
                    }
                }
            }
        }

        return movingMatrix;
    }

    public static void setMoving(Integer chartInterval, Integer interval, Integer moving, Boolean checked) {

        MovingEntity movingEntity = MovingDao.getMoving(chartInterval, interval);

        if (movingEntity == null) {
            movingEntity = new MovingEntity();
            movingEntity.chartInter = chartInterval;
            movingEntity.inter = interval;
        }

        switch (moving) {
            case 13:
                movingEntity.m13 = checked;
                break;
            case 21:
                movingEntity.m21 = checked;
                break;
            case 34:
                movingEntity.m34 = checked;
                break;
            case 55:
                movingEntity.m55 = checked;
                break;
            case 89:
                movingEntity.m89 = checked;
                break;
            case 144:
                movingEntity.m144 = checked;
                break;
            case 200:
                movingEntity.m200 = checked;
                break;
        }

        int i = getIntervalIndex(chartInterval);
        int j = getIntervalIndex(interval);
        int m = getMovingIndex(moving);

        matrix[i][j][m] = checked;
        PersistenceManager.merge(movingEntity);

    }

}
