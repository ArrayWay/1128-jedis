package com.atguigu.jedis.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

public class TestJedis {

	@Test
	public void test() {
		
		Jedis jedis = new Jedis("192.168.155.15", 6379);
		
		String pong = jedis.ping();
						
		System.out.println(pong);
		
		System.out.println("获取K1的值："+jedis.get("k1"));
		
		Set<String> keys = jedis.keys("*");
		
		for (String key : keys) {
			System.out.println(key);
		}
		
		System.out.println("是否存在K2:"+jedis.exists("k2"));
		
		System.out.println("k1的存活时间："+jedis.ttl("k1"));
		
		
		
		jedis.close();
		
	}
	@Test
	public void testString() {
		Jedis jedis=new Jedis("192.168.155.15", 6379);
		System.out.println("获取K1的值："+jedis.get("k1"));
		jedis.msetnx("k11","v12","k22","v22","k33","v33");
		System.out.println(jedis.mget("k11","k22","k33"));
		jedis.close();
	}
	@Test
	public void testList() {
		Jedis jedis=new Jedis("192.168.155.15", 6379);
		jedis.lpush("mylist", "1","2","3","4");
		List<String> list = jedis.lrange("mylist", 0, -1);
		for (String element : list) {
			System.out.println(element);
		}
		jedis.close();
	}
	@Test
	public void testSet() {
		Jedis jedis=new Jedis("192.168.155.15", 6379);
		jedis.sadd("mySet", "Jack","Marry","Tom","Tony");
		jedis.srem("mySet", "Tony");
		Set<String> smembers = jedis.smembers("mySet");	
		for (String member : smembers) {
			System.out.println(member);
		}
		jedis.close();
	}
	@Test
	public void testHash() {
		//连接指定的redis，需要ip地址和端口号
		Jedis jedis=new Jedis("192.168.155.15", 6379);
		jedis.hset("myHash", "username", "Jack");
		jedis.hset("myHash", "password", "123123");
		jedis.hset("myHash", "age", "11");
		//将多个数据封装为一个map
		Map<String, String> map=new HashMap<String, String>();
		map.put("gender", "male");
		map.put("department", "研发部");
		//批量设置多个数据
		jedis.hmset("myHash", map);
		List<String> values = jedis.hmget("myHash","username","password");
		for (String val : values) {
			System.out.println(val);
		}
		//关闭连接
		jedis.close();
	}
	@Test
	public void testZset() {
		//连接指定的redis，需要ip地址和端口号
		Jedis jedis=new Jedis("192.168.155.15", 6379);
		jedis.zadd("myZset", 100, "math");
		//将多个数据封装为一个map
		Map<String, Double> subject=new HashMap<String, Double>();
		subject.put("chinese", 88d);
		subject.put("english", 86d);
		//批量添加数据
		jedis.zadd("myZset", subject);
		Set<String> zset = jedis.zrange("myZset", 0, -1);
		for (String val : zset) {
			System.out.println(val);
		}
		//关闭连接
		jedis.close();
	}
	
	//GenericObjectPoolConfig--PoolConfig--JedisPool--jedis
	@Test
	public void testPool() {
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		System.out.println(poolConfig);
		JedisPool jedisPool = new JedisPool(poolConfig, "192.168.155.15", 6379, 60000);
		Jedis jedis = jedisPool.getResource();
		String pong = jedis.ping();
		System.out.println(pong);
		jedis.close();
		jedisPool.close();
	}
	//哨兵模式使用Jedis
	@Test
	public void testSentinel() throws Exception {
		Set<String> set = new HashSet<>();
		// set中放的是哨兵的Ip和端口
		set.add("192.168.155.15:26379");
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		JedisSentinelPool jedisSentinelPool = new JedisSentinelPool("mymaster", set, poolConfig, 60000);
		Jedis jedis = jedisSentinelPool.getResource();
		String value = jedis.get("k7");
		jedis.set("Jedis", "Jedis");
		System.out.println(value);
	}
	//集群的Jedis开发
	@Test
	public void testCluster(){
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		//Jedis Cluster will attempt to discover cluster nodes automatically
		jedisClusterNodes.add(new HostAndPort("192.168.155.15", 6389));
		JedisCluster jc = new JedisCluster(jedisClusterNodes);
		jc.set("foo", "bar");
		String value = jc.get("foo");
	}
}
