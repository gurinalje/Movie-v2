package com.example.cinema.po;

import org.junit.Test;
import java.util.Date;
import static org.junit.Assert.*;

/**
 * Movie 单元测试
 * 测试 Movie 类的 getter/setter 方法
 */
public class MovieTest {

    @Test
    public void testDefaultConstructor() {
        // 测试默认构造函数
        Movie movie = new Movie();
        assertNotNull(movie);
    }

    @Test
    public void testSettersAndGetters() {
        // 测试所有 setter 和 getter 方法
        Movie movie = new Movie();
        
        // 测试 id
        Integer id = 1;
        movie.setId(id);
        assertEquals(id, movie.getId());
        
        // 测试 name
        String name = "测试电影";
        movie.setName(name);
        assertEquals(name, movie.getName());
        
        // 测试 posterUrl
        String posterUrl = "http://example.com/poster.jpg";
        movie.setPosterUrl(posterUrl);
        assertEquals(posterUrl, movie.getPosterUrl());
        
        // 测试 director
        String director = "测试导演";
        movie.setDirector(director);
        assertEquals(director, movie.getDirector());
        
        // 测试 screenWriter
        String screenWriter = "测试编剧";
        movie.setScreenWriter(screenWriter);
        assertEquals(screenWriter, movie.getScreenWriter());
        
        // 测试 starring
        String starring = "测试主演";
        movie.setStarring(starring);
        assertEquals(starring, movie.getStarring());
        
        // 测试 type
        String type = "动作";
        movie.setType(type);
        assertEquals(type, movie.getType());
        
        // 测试 country
        String country = "中国";
        movie.setCountry(country);
        assertEquals(country, movie.getCountry());
        
        // 测试 language
        String language = "中文";
        movie.setLanguage(language);
        assertEquals(language, movie.getLanguage());
        
        // 测试 startDate
        Date startDate = new Date();
        movie.setStartDate(startDate);
        assertEquals(startDate, movie.getStartDate());
        
        // 测试 length
        Integer length = 120;
        movie.setLength(length);
        assertEquals(length, movie.getLength());
        
        // 测试 description
        String description = "测试电影描述";
        movie.setDescription(description);
        assertEquals(description, movie.getDescription());
        
        // 测试 status
        Integer status = 0;
        movie.setStatus(status);
        assertEquals(status, movie.getStatus());
        
        // 测试 islike
        Integer islike = 1;
        movie.setIslike(islike);
        assertEquals(islike, movie.getIslike());
        
        // 测试 likeCount
        Integer likeCount = 100;
        movie.setLikeCount(likeCount);
        assertEquals(likeCount, movie.getLikeCount());
    }

    @Test
    public void testSetId() {
        // 测试 setId 方法
        Movie movie = new Movie();
        Integer id = 5;
        movie.setId(id);
        assertEquals(id, movie.getId());
    }

    @Test
    public void testSetName() {
        // 测试 setName 方法
        Movie movie = new Movie();
        String name = "新电影名称";
        movie.setName(name);
        assertEquals(name, movie.getName());
    }

    @Test
    public void testSetPosterUrl() {
        // 测试 setPosterUrl 方法
        Movie movie = new Movie();
        String posterUrl = "http://example.com/new-poster.jpg";
        movie.setPosterUrl(posterUrl);
        assertEquals(posterUrl, movie.getPosterUrl());
    }

    @Test
    public void testSetDirector() {
        // 测试 setDirector 方法
        Movie movie = new Movie();
        String director = "新导演";
        movie.setDirector(director);
        assertEquals(director, movie.getDirector());
    }

    @Test
    public void testSetScreenWriter() {
        // 测试 setScreenWriter 方法
        Movie movie = new Movie();
        String screenWriter = "新编剧";
        movie.setScreenWriter(screenWriter);
        assertEquals(screenWriter, movie.getScreenWriter());
    }

    @Test
    public void testSetStarring() {
        // 测试 setStarring 方法
        Movie movie = new Movie();
        String starring = "新主演";
        movie.setStarring(starring);
        assertEquals(starring, movie.getStarring());
    }

    @Test
    public void testSetType() {
        // 测试 setType 方法
        Movie movie = new Movie();
        String type = "喜剧";
        movie.setType(type);
        assertEquals(type, movie.getType());
    }

    @Test
    public void testSetCountry() {
        // 测试 setCountry 方法
        Movie movie = new Movie();
        String country = "美国";
        movie.setCountry(country);
        assertEquals(country, movie.getCountry());
    }

    @Test
    public void testSetLanguage() {
        // 测试 setLanguage 方法
        Movie movie = new Movie();
        String language = "英语";
        movie.setLanguage(language);
        assertEquals(language, movie.getLanguage());
    }

    @Test
    public void testSetStartDate() {
        // 测试 setStartDate 方法
        Movie movie = new Movie();
        Date startDate = new Date();
        movie.setStartDate(startDate);
        assertEquals(startDate, movie.getStartDate());
    }

    @Test
    public void testSetLength() {
        // 测试 setLength 方法
        Movie movie = new Movie();
        Integer length = 90;
        movie.setLength(length);
        assertEquals(length, movie.getLength());
    }

    @Test
    public void testSetDescription() {
        // 测试 setDescription 方法
        Movie movie = new Movie();
        String description = "新电影描述";
        movie.setDescription(description);
        assertEquals(description, movie.getDescription());
    }

    @Test
    public void testSetStatus() {
        // 测试 setStatus 方法
        Movie movie = new Movie();
        Integer status = 1;
        movie.setStatus(status);
        assertEquals(status, movie.getStatus());
    }

    @Test
    public void testSetIslike() {
        // 测试 setIslike 方法
        Movie movie = new Movie();
        Integer islike = 0;
        movie.setIslike(islike);
        assertEquals(islike, movie.getIslike());
    }

    @Test
    public void testSetLikeCount() {
        // 测试 setLikeCount 方法
        Movie movie = new Movie();
        Integer likeCount = 200;
        movie.setLikeCount(likeCount);
        assertEquals(likeCount, movie.getLikeCount());
    }

    @Test
    public void testMovieWithNullValues() {
        // 测试 Movie 对象可以设置 null 值
        Movie movie = new Movie();
        
        movie.setName(null);
        assertNull(movie.getName());
        
        movie.setPosterUrl(null);
        assertNull(movie.getPosterUrl());
        
        movie.setDirector(null);
        assertNull(movie.getDirector());
        
        movie.setScreenWriter(null);
        assertNull(movie.getScreenWriter());
        
        movie.setStarring(null);
        assertNull(movie.getStarring());
        
        movie.setType(null);
        assertNull(movie.getType());
        
        movie.setCountry(null);
        assertNull(movie.getCountry());
        
        movie.setLanguage(null);
        assertNull(movie.getLanguage());
        
        movie.setStartDate(null);
        assertNull(movie.getStartDate());
        
        movie.setDescription(null);
        assertNull(movie.getDescription());
    }
}
