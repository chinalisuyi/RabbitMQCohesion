package cn.com.swordfish.mq.core.mq.mapper;

import java.io.Serializable;

/**
 * 通用的Mapper接口
 * 
 * @author chengang
 *
 */
public interface BaseMapper<T, PK extends Serializable> {
	/**
	 * 插入一个实体
	 * 
	 * @param entity
	 */
	public int insert(final T entity);

	/**
	 * 根据属性插入一个实体
	 * 
	 * @param entity
	 * @return
	 */
	public int insertSelective(final T entity);

	/**
	 * 更新一个实体
	 * 
	 * @param entity
	 */
	public int update(final T entity);

	/**
	 * 根据属性更新一个实体
	 * 
	 * @param entity
	 */
	public int updateByPrimaryKeySelective(final T entity);

	/**
	 * 删除主键对应的实体
	 * 
	 * @param id
	 */
	public int delete(final PK id);

	/**
	 * 根据ID得到某个实体,如果不存在，返回null
	 * 
	 * @param id
	 * @return
	 */
	public T getById(final PK id);
}
