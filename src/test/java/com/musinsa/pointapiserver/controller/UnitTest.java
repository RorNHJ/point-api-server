package com.musinsa.pointapiserver.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.pointapiserver.code.ErrorCode;
import com.musinsa.pointapiserver.dto.MemberReqDto;
import com.musinsa.pointapiserver.dto.MemberResDto;
import com.musinsa.pointapiserver.dto.PointCnclReqDto;
import com.musinsa.pointapiserver.dto.PointDetailDto;
import com.musinsa.pointapiserver.dto.PointReqDto;
import com.musinsa.pointapiserver.service.PointService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UnitTest {
    private static final Logger logger = LoggerFactory.getLogger(UnitTest.class);

    @Autowired
    private  MockMvc mockMvc;
    
    @Autowired
    private  ObjectMapper objectMapper;
    
    @Autowired
    private PointService pointService;
    
    //테스트 회원 번호
    private Long testMemberNo;
   
    /**
     * 모든 테스트 메소드 실행 전, 회원 등록을 먼저 진행. 회원번호는 10002로 테스트 진행.
     */
    @BeforeAll
    @DisplayName("회원 등록")
    void 테스트에_필요한_회원_등록() throws Exception {
        Long memberNo = 10002l;
        String rquestBody = objectMapper.writeValueAsString(MemberReqDto.builder().memberNo(memberNo).build());
        MvcResult result = mockMvc.perform(post("/member")
                                    .content(rquestBody)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON))
                                    .andExpect(status().isOk())
                                    .andExpect(MockMvcResultMatchers.jsonPath("$.memberNo").value(memberNo)) // 등록된 회원번호
                                    .andDo(print())
                                    .andReturn();
        
        testMemberNo = new ObjectMapper().readValue(result.getResponse().getContentAsString(), MemberResDto.class).getMemberNo();
    }
    
    
    /**
     * 단위테스트 시나리오
     * 가정: 1000 포인트 적립이 되어있다.
     * 입력: 회원번호 10002
     * 결과: totalPoint가 1000 포인트이면 성공
     */
    @Test
    @DisplayName("회원별 포인트 합계 조회")
    void 회원별_포인트_합계_조회() throws Exception {
        
        //given ( 1000포인트 적립)
        pointService.savePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(1000)).build());
        
        mockMvc.perform(get("/point/"+ testMemberNo)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberNo").value(testMemberNo))            // 등록된 회원번호
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPoint").value(BigDecimal.valueOf(1000))) // 포인트 합계
                .andReturn();
    }
    
    /**
     * 단위테스트 시나리오
     * 가정: 0포인트를 보유하고 있음.
     * 입력: 1000 포인트를 적립
     * 결과: beforePoint가 0 이고, afterPoint가 1000이면 성공.
     */
    @Test
    @DisplayName("회원별 포인트 적립")
    void 회원별_포인트_적립() throws Exception {
        
        // 1000 포인트
        BigDecimal testValue = BigDecimal.valueOf(1000);
        
        mockMvc.perform(post("/point/save")
                .content("{ \"memberNo\": "+testMemberNo+", \"point\":"+testValue+"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pointNo").exists())         // 포인트 번호
                .andExpect(MockMvcResultMatchers.jsonPath("$.beforePoint").value(0))     // 적립 전 포인트
                .andExpect(MockMvcResultMatchers.jsonPath("$.afterPoint").value(testValue))   // 적립 후 포인트
                .andReturn();
    }
    

    /**
     * 단위테스트 시나리오
     * 가정: 1000 포인트 적립이 되어있다.
     * 입력: 700 포인트 사용, 1000포인트 사용
     * 결과: 700 포인트 사용 시,  beforePoint는 1000, afterPoint는 300 이다
     *     1000 포인트 사용 시,  500 에러와 error,code가 NOT_ENOUGH_POINT 이면 성공 (포인트 부족 에러 처리)
     */
    @Test
    @DisplayName("회원별 포인트 사용")
    void 회원별_포인트_사용()  throws Exception{
        
        //given ( 1000포인트 적립)
        pointService.savePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(1000)).build());
        
        
        //given ( 700 포인트 사용,  1000포인트 사용시 포인트 부족 에러처리)
        mockMvc.perform(post("/point/use")
                .content("{ \"memberNo\": "+testMemberNo+", \"point\":700}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pointNo").exists())         // 포인트 번호
                .andExpect(MockMvcResultMatchers.jsonPath("$.beforePoint").value(1000))  // 사용 전 포인트
                .andExpect(MockMvcResultMatchers.jsonPath("$.afterPoint").value(300))   // 사용 후 포인트
                .andReturn();
      
        
        mockMvc.perform(post("/point/use")
                .content("{ \"memberNo\": "+testMemberNo+", \"point\":1000}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(ErrorCode.NOT_ENOUGH_POINT.toString()))  
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ErrorCode.NOT_ENOUGH_POINT.toString()))  
                //.andDo(print())
                .andReturn();
    }


    /**
     * 단위테스트 시나리오
     * 가정: 1500 포인트를 보유하고 있었는데, 1200 포인트, 300 포인트를 각각 사용하였다.
     * 입력: 1200포인트의 포인트번호를 취소한다
     * 결과: beforePoint는 0, afterPoint는 1200 이다
     */
    @Test
    @DisplayName("회원별 포인트 사용취소")
    void 회원별_포인트_사용취소() throws Exception {
        //given ( 1500 포인트 적립)
        pointService.savePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(1000)).build());
        pointService.savePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(500)).build());
        
        //given ( 1200,300 포인트 사용) , 1200포인트 사용 시, 취소할 포인트 번호로 메모)
        Long cancelPointNo = pointService.usePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(1200)).build()).getPointNo();
        pointService.usePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(300)).build());
        logger.debug("사용 취소할 포인트 번호 cancelPointNo : "+ cancelPointNo);

        mockMvc.perform(post("/point/cancel")
                .content("{ \"memberNo\": "+testMemberNo+", \"pointNo\":"+cancelPointNo+"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.beforePoint").value(0))      // 사용취소 전 포인트
                .andExpect(MockMvcResultMatchers.jsonPath("$.afterPoint").value(1200))      // 사용취소 후 포인트
                .andDo(print())
                .andReturn();
    }
    
    
    /**
     * 단위테스트 시나리오
     * 가정: 적립 3건, 사용 3건, 사용 건 중 사용취소 1건,  총 6건의 이력이 있다.
     * 입력: 회원번호 10002
     * 결과: 6건의 이력이 있지만 사용취소 1건을 제외한 5건이 조회되면 성공
     *      사용취소 1건 포인트 번호가 결과데이터에 포함되어있지 않으면 성공
     */
    @Test
    @DisplayName("회원별 포인트 적립/사용 내역 조회")
    void 회원별_포인트_적립_사용_내역_조회() throws Exception {
        //given (적립 3건)
        pointService.savePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(1000)).build());
        pointService.savePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(500)).build());
        pointService.savePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(1000)).build());
        
        //given (사용 3건)
        Long cancelPointNo = pointService.usePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(700)).build()).getPointNo();
        pointService.usePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(500)).build());
        pointService.usePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(500)).build());
        
        //given (사용취소 1건)
        pointService.cancelPoint(PointCnclReqDto.builder().memberNo(testMemberNo).pointNo(cancelPointNo).build());
        logger.debug("사용 취소할 포인트 번호 cancelPointNo : "+ cancelPointNo);

        MvcResult result = mockMvc.perform(get("/point/history/"+ testMemberNo)
                                    .param("page", "0")
                                    .param("size", "10")
                                    .accept(MediaType.APPLICATION_JSON))
                                    .andExpect(status().isOk())
                                    .andDo(print())
                                    .andReturn();

        List<PointDetailDto> object = new ObjectMapper().readValue(result.getResponse().getContentAsString(),new TypeReference<List<PointDetailDto>>() {});
        
        
        // 적립 3건, 사용 3건 중 사용취소 1건임으로 총 5건의 내역이 조회되야함(사용취소건은 제외되야함) 
        assertEquals(object.size(), 5);  
        // 사용취소 1건이 조회된 내역에 포함되어 있지 않으면 true 
        assertTrue(object.stream().noneMatch(e -> e.getPointNo().equals(cancelPointNo) ));
    }

}
