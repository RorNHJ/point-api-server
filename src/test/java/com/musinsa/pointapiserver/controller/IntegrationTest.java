package com.musinsa.pointapiserver.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
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
import com.musinsa.pointapiserver.dto.PointDetailDto;
import com.musinsa.pointapiserver.dto.PointResDto;


/**
 * 통합 테스트 시나리오
 * 1. 회원번호(10001)로 회원등록
 * 2. 회원번호(10001)로 1000,500,1000 포인트로 세번 적립, 
 *    -> 2-1. 사용가능한 포인트의 총합은 2500 포인트가 됨.  
 *    
 * 3. 회원번호(10001)로 700, 500,500 포인트 세번 사용
 *    -> 3-1. 이때 두번째 500포인트 사용 때의 포인트 번호 메모 -> 이 번호를 500포인트를 사용취소할거임
 *    -> 3-2. 사용한 포인트의 총합은 1700포인트임으로 사용가능한 포인트는 800 포인트임
 *    
 * 4. 회원번호(10001)로 1000포인트 사용시, 포인트가 부족하니까 NOT_ENOUGH_POINT 500번 에러
 *    
 * 5.  앞의 3번에서 메모한 500 포인트의 번호로 사용취소 
 *    -> 1300원 포인트로 되야함
 *    
 * 6. 회원별 포인트 적립/사용 내역 조회 (페이징 처리 필수, 사용취소된 내역은 조회되지 않음)
 *    -> 6-1. 적립 3건, 사용 3건 중 사용취소 1건임으로 총 5건의 내역이 조회되야함(사용취소건은 제외되야함)
 *    -> 6-2. 사용취소 1건이 조회된 내역에 포함되어 있지 않으면 true
 * 
 * */  

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);

    @Autowired
    private  MockMvc mockMvc;
    
    @Autowired
    private  ObjectMapper objectMapper;
    
    //테스트 회원 번호
    private Long testMemberNo;
    
    //사용 취소할 포인트 번호
    private Long cancelPointNo;
   
    @BeforeAll
    @DisplayName("회원 등록")
    void 테스트에_필요한_회원_등록() throws Exception {
        Long memberNo = 10001L;
        String rquestBody = objectMapper.writeValueAsString(MemberReqDto.builder().memberNo(memberNo).build());
        MvcResult result = mockMvc.perform(post("/member")
                                    .content(rquestBody)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON))
                                    .andExpect(status().isOk())
                                    .andExpect(MockMvcResultMatchers.jsonPath("$.memberNo").value(memberNo)) // 등록된 회원번호
                                    .andReturn();
        
        testMemberNo = new ObjectMapper().readValue(result.getResponse().getContentAsString(), MemberResDto.class).getMemberNo();
    }
    
    
    @Test
    @DisplayName("통합 테스트")
    @Order(1)
    void 통합_테스트() throws Exception {
        MvcResult result = null;
        
        /** ======= 2. 회원번호(10001)로 1000,500,1000 포인트로 세번 적립,=========*/
        
        mockMvc.perform(post("/point/save")
               .content("{ \"memberNo\": "+testMemberNo+", \"point\":1000}")
               .contentType(MediaType.APPLICATION_JSON)
               .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.pointNo").exists())         // 포인트 번호
               .andExpect(MockMvcResultMatchers.jsonPath("$.beforePoint").value(0))     // 적립 전 포인트
               .andExpect(MockMvcResultMatchers.jsonPath("$.afterPoint").value(1000))   // 적립 후 포인트
               .andReturn();
        
        
        mockMvc.perform(post("/point/save")
                .content("{ \"memberNo\": "+testMemberNo+", \"point\":500}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pointNo").exists())          // 포인트 번호
                .andExpect(MockMvcResultMatchers.jsonPath("$.beforePoint").value(1000))   // 적립 전 포인트
                .andExpect(MockMvcResultMatchers.jsonPath("$.afterPoint").value(1500))    // 적립 후 포인트
                .andReturn();
        
        mockMvc.perform(post("/point/save")
                .content("{ \"memberNo\": "+testMemberNo+", \"point\":1000}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pointNo").exists())         // 포인트 번호
                .andExpect(MockMvcResultMatchers.jsonPath("$.beforePoint").value(1500))  // 적립 전 포인트
                .andExpect(MockMvcResultMatchers.jsonPath("$.afterPoint").value(2500))   // 적립 후 포인트
                .andReturn();
        
        
        
        
        /** ======= 2-1. 사용가능한 포인트의 총합은 2500 포인트가 됨 =========*/
        mockMvc.perform(get("/point/"+ testMemberNo)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberNo").value(testMemberNo))    // 등록된 회원번호
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPoint").value(2500))          // 포인트 합계
                .andReturn();
        
        
        /** ======= 3. 회원번호(10001)로 700, 500,500 포인트 세번 사용=========*/
        mockMvc.perform(post("/point/use")
                .content("{ \"memberNo\": "+testMemberNo+", \"point\":700}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pointNo").exists())         // 포인트 번호
                .andExpect(MockMvcResultMatchers.jsonPath("$.beforePoint").value(2500))  // 사용 전 포인트
                .andExpect(MockMvcResultMatchers.jsonPath("$.afterPoint").value(1800))   // 사용 후 포인트
                .andReturn();
        
        result = mockMvc.perform(post("/point/use")
                                    .content("{ \"memberNo\": "+testMemberNo+", \"point\":500}")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON))
                                    .andExpect(status().isOk())
                                    .andExpect(MockMvcResultMatchers.jsonPath("$.pointNo").exists())         // 포인트 번호
                                    .andExpect(MockMvcResultMatchers.jsonPath("$.beforePoint").value(1800))  // 사용 전 포인트
                                    .andExpect(MockMvcResultMatchers.jsonPath("$.afterPoint").value(1300))   // 사용 후 포인트
                                    .andReturn();
        
        /** ======= 3-1. 이때 두번째 500포인트 사용 때의 포인트 번호 메모 -> 이 번호를 사용취소할거임 =========*/
        cancelPointNo = new ObjectMapper().readValue(result.getResponse().getContentAsString(), PointResDto.class).getPointNo();
        logger.debug("사용 취소할 포인트 번호 cancelPointNo : "+ cancelPointNo);
        
        mockMvc.perform(post("/point/use")
                .content("{ \"memberNo\": "+testMemberNo+", \"point\":500}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pointNo").exists())         // 포인트 번호
                .andExpect(MockMvcResultMatchers.jsonPath("$.beforePoint").value(1300))  // 사용 전 포인트
                .andExpect(MockMvcResultMatchers.jsonPath("$.afterPoint").value(800))    // 사용 후 포인트
                .andReturn();
        
        /** ======= 3-2. 사용한 포인트의 총합은 1700포인트임으로 사용가능한 포인트는 800 포인트임 =========*/
        mockMvc.perform(get("/point/"+ testMemberNo)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberNo").value(testMemberNo)) // 등록된 회원번호
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPoint").value(800))        // 포인트 합계
                .andReturn();
        
        
        /** ======= 4. 회원번호(10001)로 1000포인트 사용시, 포인트가 부족하니까 NOT_ENOUGH_POINT 500번 에러 =========*/
        mockMvc.perform(post("/point/use")
                .content("{ \"memberNo\": "+testMemberNo+", \"point\":1000}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(ErrorCode.NOT_ENOUGH_POINT.toString()))  
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ErrorCode.NOT_ENOUGH_POINT.toString()))  
                .andReturn();
        
        /** ======= 5.  앞의 3번에서 메모한 포인트 번호로 사용취소 =========*/
        mockMvc.perform(post("/point/cancel")
                .content("{ \"memberNo\": "+testMemberNo+", \"pointNo\":"+cancelPointNo+"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.beforePoint").value(800))      // 사용취소 전 포인트
                .andExpect(MockMvcResultMatchers.jsonPath("$.afterPoint").value(1300))      // 사용취소 후 포인트
                .andReturn();
        
        /** ======= 6.  회원별 포인트 적립/사용 내역 조회 (페이징 처리 필수, 사용취소된 내역은 조회되지 않음) =========*/
        result = mockMvc.perform(get("/point/history/"+ testMemberNo)
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn();
        
        List<PointDetailDto> object = new ObjectMapper().readValue(result.getResponse().getContentAsString(),new TypeReference<List<PointDetailDto>>() {});
        
        
        /** =======  6-1. 적립 3건, 사용 3건 중 사용취소 1건임으로 총 5건의 내역이 조회되야함(사용취소건은 제외되야함) =========*/
        assertEquals(object.size(), 5);  
        /** =======  6-2. 사용취소 1건이 조회된 내역에 포함되어 있지 않으면 true =========*/
        assertTrue(object.stream().noneMatch(e -> e.getPointNo().equals(cancelPointNo) ));
        
    }
    
    
    

}
