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
    
    //????????? ?????? ??????
    private Long testMemberNo;
   
    /**
     * ?????? ????????? ????????? ?????? ???, ?????? ????????? ?????? ??????. ??????????????? 10002??? ????????? ??????.
     */
    @BeforeAll
    @DisplayName("?????? ??????")
    void ????????????_?????????_??????_??????() throws Exception {
        Long memberNo = 10002l;
        String rquestBody = objectMapper.writeValueAsString(MemberReqDto.builder().memberNo(memberNo).build());
        MvcResult result = mockMvc.perform(post("/member")
                                    .content(rquestBody)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON))
                                    .andExpect(status().isOk())
                                    .andExpect(MockMvcResultMatchers.jsonPath("$.memberNo").value(memberNo)) // ????????? ????????????
                                    .andDo(print())
                                    .andReturn();
        
        testMemberNo = new ObjectMapper().readValue(result.getResponse().getContentAsString(), MemberResDto.class).getMemberNo();
    }
    
    
    /**
     * ??????????????? ????????????
     * ??????: 1000 ????????? ????????? ????????????.
     * ??????: ???????????? 10002
     * ??????: totalPoint??? 1000 ??????????????? ??????
     */
    @Test
    @DisplayName("????????? ????????? ?????? ??????")
    void ?????????_?????????_??????_??????() throws Exception {
        
        //given ( 1000????????? ??????)
        pointService.savePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(1000)).build());
        
        mockMvc.perform(get("/point/"+ testMemberNo)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberNo").value(testMemberNo))            // ????????? ????????????
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPoint").value(BigDecimal.valueOf(1000))) // ????????? ??????
                .andReturn();
    }
    
    /**
     * ??????????????? ????????????
     * ??????: 0???????????? ???????????? ??????.
     * ??????: 1000 ???????????? ??????
     * ??????: beforePoint??? 0 ??????, afterPoint??? 1000?????? ??????.
     */
    @Test
    @DisplayName("????????? ????????? ??????")
    void ?????????_?????????_??????() throws Exception {
        
        // 1000 ?????????
        BigDecimal testValue = BigDecimal.valueOf(1000);
        
        mockMvc.perform(post("/point/save")
                .content("{ \"memberNo\": "+testMemberNo+", \"point\":"+testValue+"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pointNo").exists())         // ????????? ??????
                .andExpect(MockMvcResultMatchers.jsonPath("$.beforePoint").value(0))     // ?????? ??? ?????????
                .andExpect(MockMvcResultMatchers.jsonPath("$.afterPoint").value(testValue))   // ?????? ??? ?????????
                .andReturn();
    }
    

    /**
     * ??????????????? ????????????
     * ??????: 1000 ????????? ????????? ????????????.
     * ??????: 700 ????????? ??????, 1000????????? ??????
     * ??????: 700 ????????? ?????? ???,  beforePoint??? 1000, afterPoint??? 300 ??????
     *     1000 ????????? ?????? ???,  500 ????????? error,code??? NOT_ENOUGH_POINT ?????? ?????? (????????? ?????? ?????? ??????)
     */
    @Test
    @DisplayName("????????? ????????? ??????")
    void ?????????_?????????_??????()  throws Exception{
        
        //given ( 1000????????? ??????)
        pointService.savePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(1000)).build());
        
        
        //given ( 700 ????????? ??????,  1000????????? ????????? ????????? ?????? ????????????)
        mockMvc.perform(post("/point/use")
                .content("{ \"memberNo\": "+testMemberNo+", \"point\":700}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pointNo").exists())         // ????????? ??????
                .andExpect(MockMvcResultMatchers.jsonPath("$.beforePoint").value(1000))  // ?????? ??? ?????????
                .andExpect(MockMvcResultMatchers.jsonPath("$.afterPoint").value(300))   // ?????? ??? ?????????
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
     * ??????????????? ????????????
     * ??????: 1500 ???????????? ???????????? ????????????, 1200 ?????????, 300 ???????????? ?????? ???????????????.
     * ??????: 1200???????????? ?????????????????? ????????????
     * ??????: beforePoint??? 0, afterPoint??? 1200 ??????
     */
    @Test
    @DisplayName("????????? ????????? ????????????")
    void ?????????_?????????_????????????() throws Exception {
        //given ( 1500 ????????? ??????)
        pointService.savePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(1000)).build());
        pointService.savePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(500)).build());
        
        //given ( 1200,300 ????????? ??????) , 1200????????? ?????? ???, ????????? ????????? ????????? ??????)
        Long cancelPointNo = pointService.usePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(1200)).build()).getPointNo();
        pointService.usePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(300)).build());
        logger.debug("?????? ????????? ????????? ?????? cancelPointNo : "+ cancelPointNo);

        mockMvc.perform(post("/point/cancel")
                .content("{ \"memberNo\": "+testMemberNo+", \"pointNo\":"+cancelPointNo+"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.beforePoint").value(0))      // ???????????? ??? ?????????
                .andExpect(MockMvcResultMatchers.jsonPath("$.afterPoint").value(1200))      // ???????????? ??? ?????????
                .andDo(print())
                .andReturn();
    }
    
    
    /**
     * ??????????????? ????????????
     * ??????: ?????? 3???, ?????? 3???, ?????? ??? ??? ???????????? 1???,  ??? 6?????? ????????? ??????.
     * ??????: ???????????? 10002
     * ??????: 6?????? ????????? ????????? ???????????? 1?????? ????????? 5?????? ???????????? ??????
     *      ???????????? 1??? ????????? ????????? ?????????????????? ?????????????????? ????????? ??????
     */
    @Test
    @DisplayName("????????? ????????? ??????/?????? ?????? ??????")
    void ?????????_?????????_??????_??????_??????_??????() throws Exception {
        //given (?????? 3???)
        pointService.savePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(1000)).build());
        pointService.savePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(500)).build());
        pointService.savePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(1000)).build());
        
        //given (?????? 3???)
        Long cancelPointNo = pointService.usePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(700)).build()).getPointNo();
        pointService.usePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(500)).build());
        pointService.usePoint(PointReqDto.builder().memberNo(testMemberNo).point(BigDecimal.valueOf(500)).build());
        
        //given (???????????? 1???)
        pointService.cancelPoint(PointCnclReqDto.builder().memberNo(testMemberNo).pointNo(cancelPointNo).build());
        logger.debug("?????? ????????? ????????? ?????? cancelPointNo : "+ cancelPointNo);

        MvcResult result = mockMvc.perform(get("/point/history/"+ testMemberNo)
                                    .param("page", "0")
                                    .param("size", "10")
                                    .accept(MediaType.APPLICATION_JSON))
                                    .andExpect(status().isOk())
                                    .andDo(print())
                                    .andReturn();

        List<PointDetailDto> object = new ObjectMapper().readValue(result.getResponse().getContentAsString(),new TypeReference<List<PointDetailDto>>() {});
        
        
        // ?????? 3???, ?????? 3??? ??? ???????????? 1???????????? ??? 5?????? ????????? ???????????????(?????????????????? ???????????????) 
        assertEquals(object.size(), 5);  
        // ???????????? 1?????? ????????? ????????? ???????????? ?????? ????????? true 
        assertTrue(object.stream().noneMatch(e -> e.getPointNo().equals(cancelPointNo) ));
    }

}
