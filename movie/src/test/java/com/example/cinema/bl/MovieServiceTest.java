package com.example.cinema.bl;

import com.example.cinema.bl.management.MovieService;
import com.example.cinema.blImpl.management.movie.MovieServiceImpl;
import com.example.cinema.blImpl.management.schedule.ScheduleServiceForBl;
import com.example.cinema.data.management.MovieMapper;
import com.example.cinema.po.Movie;
import com.example.cinema.po.ScheduleItem;
import com.example.cinema.vo.MovieBatchOffForm;
import com.example.cinema.vo.MovieForm;
import com.example.cinema.vo.ResponseVO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * MovieService 集成测试（Mock 数据库）
 * 测试 MovieServiceImpl 类的业务逻辑
 */
@RunWith(MockitoJUnitRunner.class)
public class MovieServiceTest {

    @Mock
    private MovieMapper movieMapper;

    @Mock
    private ScheduleServiceForBl scheduleServiceForBl;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie testMovie;
    private MovieForm testMovieForm;
    private ScheduleItem testScheduleItem;

    @Before
    public void setUp() {
        // 初始化测试数据
        testMovie = new Movie();
        testMovie.setId(1);
        testMovie.setName("测试电影");
        testMovie.setPosterUrl("http://example.com/poster.jpg");
        testMovie.setDirector("测试导演");
        testMovie.setScreenWriter("测试编剧");
        testMovie.setStarring("测试主演");
        testMovie.setType("动作");
        testMovie.setCountry("中国");
        testMovie.setLanguage("中文");
        testMovie.setStartDate(new Date());
        testMovie.setLength(120);
        testMovie.setDescription("测试电影描述");
        testMovie.setStatus(0);
        testMovie.setIslike(0);
        testMovie.setLikeCount(100);

        testMovieForm = new MovieForm();
        testMovieForm.setName("测试电影");
        testMovieForm.setPosterUrl("http://example.com/poster.jpg");
        testMovieForm.setDirector("测试导演");
        testMovieForm.setScreenWriter("测试编剧");
        testMovieForm.setStarring("测试主演");
        testMovieForm.setType("动作");
        testMovieForm.setCountry("中国");
        testMovieForm.setLanguage("中文");
        testMovieForm.setStartDate(new Date());
        testMovieForm.setLength(120);
        testMovieForm.setDescription("测试电影描述");

        testScheduleItem = new ScheduleItem();
        testScheduleItem.setId(1);
        testScheduleItem.setMovieId(1);
        testScheduleItem.setMovieName("测试电影");
        testScheduleItem.setHallId(1);
        testScheduleItem.setHallName("1号厅");
        testScheduleItem.setStartTime(new Date());
        testScheduleItem.setEndTime(new Date(System.currentTimeMillis() + 7200000));
        testScheduleItem.setFare(35.0);
    }

    @Test
    public void testAddMovieSuccess() {
        // 测试上架电影成功
        when(movieMapper.insertOneMovie(any(MovieForm.class))).thenReturn(1);

        ResponseVO response = movieService.addMovie(testMovieForm);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        verify(movieMapper, times(1)).insertOneMovie(any(MovieForm.class));
    }

    @Test
    public void testAddMovieFailure() {
        // 测试上架电影失败
        when(movieMapper.insertOneMovie(any(MovieForm.class)))
                .thenThrow(new RuntimeException("数据库错误"));

        ResponseVO response = movieService.addMovie(testMovieForm);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("失败", response.getMessage());
    }

    @Test
    public void testSearchOneMovieByIdAndUserIdSuccess() {
        // 测试根据ID和用户ID搜索电影成功
        when(movieMapper.selectMovieByIdAndUserId(1, 1)).thenReturn(testMovie);

        ResponseVO response = movieService.searchOneMovieByIdAndUserId(1, 1);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(response.getContent());
    }

    @Test
    public void testSearchOneMovieByIdAndUserIdNotFound() {
        // 测试根据ID和用户ID搜索电影未找到
        when(movieMapper.selectMovieByIdAndUserId(anyInt(), anyInt())).thenReturn(null);

        ResponseVO response = movieService.searchOneMovieByIdAndUserId(999, 999);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNull(response.getContent());
    }

    @Test
    public void testSearchOneMovieByIdAndUserIdFailure() {
        // 测试根据ID和用户ID搜索电影失败
        when(movieMapper.selectMovieByIdAndUserId(anyInt(), anyInt()))
                .thenThrow(new RuntimeException("数据库错误"));

        ResponseVO response = movieService.searchOneMovieByIdAndUserId(1, 1);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("失败", response.getMessage());
    }

    @Test
    public void testSearchAllMovieSuccess() {
        // 测试搜索全部电影成功
        List<Movie> movieList = Arrays.asList(testMovie);
        when(movieMapper.selectAllMovie()).thenReturn(movieList);

        ResponseVO response = movieService.searchAllMovie();

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(response.getContent());
    }

    @Test
    public void testSearchAllMovieFailure() {
        // 测试搜索全部电影失败
        when(movieMapper.selectAllMovie()).thenThrow(new RuntimeException("数据库错误"));

        ResponseVO response = movieService.searchAllMovie();

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("失败", response.getMessage());
    }

    @Test
    public void testSearchOtherMoviesExcludeOffSuccess() {
        // 测试搜索全部电影（不包括已下架）成功
        List<Movie> movieList = Arrays.asList(testMovie);
        when(movieMapper.selectOtherMoviesExcludeOff()).thenReturn(movieList);

        ResponseVO response = movieService.searchOtherMoviesExcludeOff();

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(response.getContent());
    }

    @Test
    public void testSearchOtherMoviesExcludeOffFailure() {
        // 测试搜索全部电影（不包括已下架）失败
        when(movieMapper.selectOtherMoviesExcludeOff()).thenThrow(new RuntimeException("数据库错误"));

        ResponseVO response = movieService.searchOtherMoviesExcludeOff();

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("失败", response.getMessage());
    }

    @Test
    public void testGetMovieByKeywordWithKeyword() {
        // 测试根据关键字搜索电影
        List<Movie> movieList = Arrays.asList(testMovie);
        when(movieMapper.selectMovieByKeyword("测试")).thenReturn(movieList);

        ResponseVO response = movieService.getMovieByKeyword("测试");

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(response.getContent());
    }

    @Test
    public void testGetMovieByKeywordWithEmptyKeyword() {
        // 测试空关键字搜索电影
        List<Movie> movieList = Arrays.asList(testMovie);
        when(movieMapper.selectAllMovie()).thenReturn(movieList);

        ResponseVO response = movieService.getMovieByKeyword("");

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(response.getContent());
    }

    @Test
    public void testGetMovieByKeywordWithNullKeyword() {
        // 测试null关键字搜索电影
        List<Movie> movieList = Arrays.asList(testMovie);
        when(movieMapper.selectAllMovie()).thenReturn(movieList);

        ResponseVO response = movieService.getMovieByKeyword(null);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(response.getContent());
    }

    @Test
    public void testPullOfBatchOfMovieSuccess() {
        // 测试批量下架电影成功
        MovieBatchOffForm batchOffForm = new MovieBatchOffForm();
        batchOffForm.setMovieIdList(Arrays.asList(1, 2, 3));

        // 模拟没有后续排片
        when(scheduleServiceForBl.getScheduleByMovieIdList(anyList())).thenReturn(Arrays.asList());
        when(movieMapper.updateMovieStatusBatch(anyList())).thenReturn(3);

        ResponseVO response = movieService.pullOfBatchOfMovie(batchOffForm);

        assertNotNull(response);
        assertTrue(response.getSuccess());
    }

    @Test
    public void testPullOfBatchOfMovieWithFutureSchedule() {
        // 测试批量下架电影失败（有后续排片）
        MovieBatchOffForm batchOffForm = new MovieBatchOffForm();
        batchOffForm.setMovieIdList(Arrays.asList(1, 2, 3));

        // 模拟有后续排片
        ScheduleItem futureSchedule = new ScheduleItem();
        futureSchedule.setEndTime(new Date(System.currentTimeMillis() + 86400000)); // 明天
        when(scheduleServiceForBl.getScheduleByMovieIdList(anyList())).thenReturn(Arrays.asList(futureSchedule));

        ResponseVO response = movieService.pullOfBatchOfMovie(batchOffForm);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("有电影后续仍有排片或已有观众购票且未使用", response.getMessage());
    }

    @Test
    public void testPullOfBatchOfMovieFailure() {
        // 测试批量下架电影失败
        MovieBatchOffForm batchOffForm = new MovieBatchOffForm();
        batchOffForm.setMovieIdList(Arrays.asList(1, 2, 3));

        when(scheduleServiceForBl.getScheduleByMovieIdList(anyList()))
                .thenThrow(new RuntimeException("数据库错误"));

        ResponseVO response = movieService.pullOfBatchOfMovie(batchOffForm);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("失败", response.getMessage());
    }

    @Test
    public void testUpdateMovieSuccess() {
        // 测试更新电影信息成功
        testMovieForm.setId(1);

        // 模拟没有后续排片
        when(scheduleServiceForBl.getScheduleByMovieIdList(anyList())).thenReturn(Arrays.asList());
        when(movieMapper.updateMovie(any(MovieForm.class))).thenReturn(1);

        ResponseVO response = movieService.updateMovie(testMovieForm);

        assertNotNull(response);
        assertTrue(response.getSuccess());
    }

    @Test
    public void testUpdateMovieWithFutureSchedule() {
        // 测试更新电影信息失败（有后续排片）
        testMovieForm.setId(1);

        // 模拟有后续排片
        ScheduleItem futureSchedule = new ScheduleItem();
        futureSchedule.setEndTime(new Date(System.currentTimeMillis() + 86400000)); // 明天
        when(scheduleServiceForBl.getScheduleByMovieIdList(anyList())).thenReturn(Arrays.asList(futureSchedule));

        ResponseVO response = movieService.updateMovie(testMovieForm);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("有电影后续仍有排片或已有观众购票且未使用", response.getMessage());
    }

    @Test
    public void testUpdateMovieFailure() {
        // 测试更新电影信息失败
        testMovieForm.setId(1);

        when(scheduleServiceForBl.getScheduleByMovieIdList(anyList()))
                .thenThrow(new RuntimeException("数据库错误"));

        ResponseVO response = movieService.updateMovie(testMovieForm);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("失败", response.getMessage());
    }

    @Test
    public void testGetMovieByIdSuccess() {
        // 测试根据ID获取电影成功
        when(movieMapper.selectMovieById(1)).thenReturn(testMovie);

        Movie movie = movieService.getMovieById(1);

        assertNotNull(movie);
        assertEquals(testMovie.getId(), movie.getId());
        assertEquals(testMovie.getName(), movie.getName());
    }

    @Test
    public void testGetMovieByIdNotFound() {
        // 测试根据ID获取电影未找到
        when(movieMapper.selectMovieById(anyInt())).thenReturn(null);

        Movie movie = movieService.getMovieById(999);

        assertNull(movie);
    }

    @Test
    public void testGetMovieByIdFailure() {
        // 测试根据ID获取电影失败
        when(movieMapper.selectMovieById(anyInt()))
                .thenThrow(new RuntimeException("数据库错误"));

        Movie movie = movieService.getMovieById(1);

        assertNull(movie);
    }
}
