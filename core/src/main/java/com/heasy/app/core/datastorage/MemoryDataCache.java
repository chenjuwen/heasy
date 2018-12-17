package com.heasy.app.core.datastorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2018/2/11.
 */
public class MemoryDataCache implements DataCache {
    private static Logger logger = LoggerFactory.getLogger(MemoryDataCache.class);
    private Map<String, Object> dataMap = new ConcurrentHashMap<>();
    private ReentrantLock lock = new ReentrantLock();

    /**
     * 缓存对象
     * @param key
     * @param value
     * @return
     * 		true成功，false失败
     */
    @Override
    public boolean set(String key, Object value) {
        lock.lock();
        try{
            dataMap.put(key, value);
        }finally{
            lock.unlock();
        }
        return true;
    }

    /**
     * 缓存对象（含超时时间）
     * @param key
     * @param value
     * @param expireSeconds 多少秒后过期
     * @return
     * 		true成功，false失败
     */
    @Override
    public boolean set(String key, Object value, int expireSeconds) {
        lock.lock();
        try{
            ValueItem item = new ValueItem(value, expireSeconds);
            dataMap.put(key, item);
        }finally{
            lock.unlock();
        }
        return true;
    }

    /**
     * 添加对象到缓存
     * @param key
     * @param value
     * @return
     * 		true成功，false失败
     */
    @Override
    public boolean add(String key, Object value) {
        lock.lock();
        try{
            if(!dataMap.containsKey(key)) {
                dataMap.put(key, value);
                return true;
            }else{
                logger.debug("already exists key: " + key);
                return false;
            }
        }finally{
            lock.unlock();
        }
    }

    /**
     * 添加对象到缓存（含超时时间）
     * @param key
     * @param value
     * @param expireSeconds 多少秒后过期
     * @return
     * 		true成功，false失败
     */
    @Override
    public boolean add(String key, Object value, int expireSeconds) {
        lock.lock();
        try{
            if(!dataMap.containsKey(key)) {
                ValueItem item = new ValueItem(value, expireSeconds);
                dataMap.put(key, item);
                return true;
            }else{
                logger.debug("already exists key: " + key);
                return false;
            }
        }finally{
            lock.unlock();
        }
    }

    /**
     * 删除缓存对象
     * @param key
     * @return
     * 		true成功，false失败
     */
    @Override
    public boolean delete(String key) {
        if(dataMap.containsKey(key)) {
            dataMap.remove(key);
            return true;
        }else {
            logger.debug("not exists key: " + key);
            return false;
        }
    }

    /**
     * 批量删除缓存对象
     * @param keys
     * @return
     * 		true成功，false失败
     */
    @Override
    public boolean delete(String... keys) {
        if(keys != null){
            for(String key : keys){
                dataMap.remove(key);
            }
        }
        return true;
    }

    /**
     * 获取缓存对象
     * @param key
     * @return
     */
    @Override
    public Object get(String key) {
        if(!exists(key)){
            logger.debug("not exists key: " + key);
            return null;
        }

        Object object = dataMap.get(key);
        if(object == null){
            return null;
        }else{
            if(object instanceof AtomicLong) {
                AtomicLong atomicLong = (AtomicLong)object;
                return new Long(atomicLong.get());

            }else if(object instanceof ValueItem){
                    ValueItem item = (ValueItem)object;
                    if(item.getExpireTimeMillis() <= System.currentTimeMillis()){ //已过期
                        logger.debug("key already :expired " + key);
                        dataMap.remove(key);
                        return null;
                    }else{
                        if(item.getValue() instanceof AtomicLong) {
                            AtomicLong atomicLong = (AtomicLong)item.getValue();
                            return new Long(atomicLong.get());
                        }else{
                            return item.getValue();
                        }
                    }
            }else{
                return object;
            }
        }
    }

    /**
     * 检查缓存对象是否存在
     * @param key
     * @return
     */
    @Override
    public boolean exists(String key) {
        return dataMap.containsKey(key);
    }

    /**
     * 计数器自增1
     * @param key
     * @return
     */
    @Override
    public long incr(String key) {
        long initValue = 0L;
        lock.lock();
        try{
            if(!exists(key)){
                storeCounter(key, initValue);
                return initValue;
            }else{
                Object object = dataMap.get(key);
                if(object == null){
                    logger.debug("not a counter: " + key);
                    return initValue;
                }else{
                    if(object instanceof AtomicLong){
                        AtomicLong atomicLong = (AtomicLong)object;
                        long value = atomicLong.incrementAndGet();
                        dataMap.remove(key);
                        storeCounter(key, value);
                        return value;
                    }else if(object instanceof ValueItem){
                        ValueItem item = (ValueItem)object;
                        if(item.getExpireTimeMillis() <= System.currentTimeMillis()){ //已过期
                            logger.debug("key already expired: " + key);
                            dataMap.remove(key);
                            return initValue;
                        }else{
                            if(item.getValue() instanceof AtomicLong){
                                AtomicLong atomicLong = (AtomicLong)item.getValue();
                                long value = atomicLong.incrementAndGet();
                                dataMap.remove(key);
                                storeCounter(key, value, item.getExpireTimeMillis());
                                return value;
                            }else{
                                logger.debug("not a counter: " + key);
                                return initValue;
                            }
                        }
                    }else{
                        logger.debug("not a counter: " + key);
                        return initValue;
                    }
                }
            }
        }finally{
            lock.unlock();
        }
    }

    /**
     * 计数器自减1
     * @param key
     * @return
     */
    @Override
    public long decr(String key) {
        long initValue = 0L;
        lock.lock();
        try{
            if(!exists(key)){
                storeCounter(key, initValue);
                return initValue;
            }else{
                Object object = dataMap.get(key);
                if(object == null){
                    logger.debug("not a counter: " + key);
                    return initValue;
                }else{
                    if(object instanceof AtomicLong){
                        AtomicLong atomicLong = (AtomicLong)object;
                        long value = atomicLong.decrementAndGet();
                        if(value < initValue){
                            value = initValue;
                        }
                        dataMap.remove(key);
                        storeCounter(key, value);
                        return value;
                    }else if(object instanceof ValueItem){
                        ValueItem item = (ValueItem)object;
                        if(item.getExpireTimeMillis() <= System.currentTimeMillis()){ //已过期
                            logger.debug("key already expired: " + key);
                            dataMap.remove(key);
                            return initValue;
                        }else{
                            if(item.getValue() instanceof AtomicLong){
                                AtomicLong atomicLong = (AtomicLong)item.getValue();
                                long value = atomicLong.decrementAndGet();
                                if(value < initValue){
                                    value = initValue;
                                }
                                dataMap.remove(key);
                                storeCounter(key, value, item.getExpireTimeMillis());
                                return value;
                            }else{
                                logger.debug("not a counter: " + key);
                                return initValue;
                            }
                        }
                    }else{
                        logger.debug("not a counter: " + key);
                        return initValue;
                    }
                }
            }
        }finally{
            lock.unlock();
        }
    }

    /**
     * 保存一个计数器到缓存
     * @param key
     * @param counter
     * @return
     */
    @Override
    public boolean storeCounter(String key, long counter) {
        lock.lock();
        try{
            if(!exists(key)) {
                AtomicLong atomicLong = new AtomicLong(counter);
                dataMap.put(key, atomicLong);
                return true;
            }else{
                return false;
            }
        }finally{
            lock.unlock();
        }
    }

    /**
     * 保存一个计数器到缓存（含过期时间）
     * @param key
     * @param counter
     * @param expireSeconds 多少秒后过期
     * @return
     */
    @Override
    public boolean storeCounter(String key, long counter, int expireSeconds) {
        lock.lock();
        try{
            if(!exists(key)) {
                AtomicLong atomicLong = new AtomicLong(counter);
                ValueItem item = new ValueItem(atomicLong, expireSeconds);
                dataMap.put(key, item);
                return true;
            }else{
                return false;
            }
        }finally{
            lock.unlock();
        }
    }

    /**
     * 保存一个计数器到缓存（含过期时间）
     * @param key
     * @param counter
     * @param expireTimeMillis 过期毫秒值
     * @return
     */
    @Override
    public boolean storeCounter(String key, long counter, long expireTimeMillis) {
        lock.lock();
        try{
            if(!exists(key)) {
                AtomicLong atomicLong = new AtomicLong(counter);
                ValueItem item = new ValueItem(atomicLong, expireTimeMillis);
                dataMap.put(key, item);
                return true;
            }else{
                return false;
            }
        }finally{
            lock.unlock();
        }
    }

    /**
     * 获取计数器
     * @param key
     * @return
     */
    @Override
    public Long getCounter(String key) {
        Object object = get(key);
        if(object != null && object instanceof Long){
            return (Long)object;
        }
        return 0L;
    }

    /**
     * 清除缓存中的所有数据
     */
    @Override
    public void clear() {
        lock.lock();
        try{
            dataMap.clear();
        }finally{
            lock.unlock();
        }
    }

    class ValueItem implements Serializable{
        //值
        private Object value;
        //过期毫秒值
        private long expireTimeMillis;

        public ValueItem(Object value, long expireTimeMillis){
            this.value = value;
            this.expireTimeMillis = expireTimeMillis;
        }

        public ValueItem(Object value, int expireSeconds){
            Calendar calendar = Calendar.getInstance();
            System.out.print("before: " + calendar.get(Calendar.MILLISECOND));
            calendar.add(Calendar.SECOND, expireSeconds);

            this.value = value;
            this.expireTimeMillis = calendar.getTimeInMillis();
            System.out.print("after: " + expireTimeMillis);
        }

        public Object getValue() {
            return value;
        }

        public long getExpireTimeMillis() {
            return expireTimeMillis;
        }
    }

}
