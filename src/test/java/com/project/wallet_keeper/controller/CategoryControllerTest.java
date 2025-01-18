package com.project.wallet_keeper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.wallet_keeper.dto.category.CategoryResponseDto;
import com.project.wallet_keeper.entity.ExpenseCategory;
import com.project.wallet_keeper.entity.IncomeCategory;
import com.project.wallet_keeper.dto.category.CreateCategoryDto;
import com.project.wallet_keeper.exception.transaction.TransactionCategoryNotFoundException;
import com.project.wallet_keeper.util.auth.CustomAuthenticationEntryPoint;
import com.project.wallet_keeper.util.jwt.TokenProvider;
import com.project.wallet_keeper.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@WithMockUser
@DisplayName("CategoryController 테스트")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private CategoryController categoryController;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private static String categoryName = "카테고리";

    @Test
    @DisplayName("수입 카테고리 저장 성공")
    void createIncomeCategory() throws Exception {
        // given
        IncomeCategory category = new IncomeCategory(categoryName);
        CreateCategoryDto categoryDto = new CreateCategoryDto(categoryName);
        given(categoryService.createIncomeCategory(any())).willReturn(category);

        // when
        ResultActions result = mockMvc.perform(post("/api/transaction/category/income")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("수입 카테고리 저장 실패: 카테고리명 누락")
    void createIncomeCategoryFail() throws Exception {
        // given
        IncomeCategory category = new IncomeCategory(categoryName);
        CreateCategoryDto categoryDto = new CreateCategoryDto("");
        given(categoryService.createIncomeCategory(any())).willReturn(category);

        // when
        ResultActions result = mockMvc.perform(post("/api/transaction/category/income")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("지출 카테고리 저장 성공")
    void createExpenseCategory() throws Exception {
        // given
        ExpenseCategory category = new ExpenseCategory(categoryName);
        CreateCategoryDto categoryDto = new CreateCategoryDto(categoryName);
        given(categoryService.createExpenseCategory(any())).willReturn(category);

        // when
        ResultActions result = mockMvc.perform(post("/api/transaction/category/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("지출 카테고리 저장 실패: 카테고리명 누락")
    void createExpenseCategoryFail() throws Exception {
        // given
        ExpenseCategory category = new ExpenseCategory(categoryName);
        CreateCategoryDto categoryDto = new CreateCategoryDto("");
        given(categoryService.createExpenseCategory(any())).willReturn(category);

        // when
        ResultActions result = mockMvc.perform(post("/api/transaction/category/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("수입 카테고리 목록 조회 성공")
    void getIncomeCategoryList() throws Exception {
        // given
        List<CategoryResponseDto> list = new ArrayList<>();
        IncomeCategory category = new IncomeCategory(categoryName);
        list.add(new CategoryResponseDto(category));

        given(categoryService.getIncomeCategories()).willReturn(list);

        // when
        ResultActions result = mockMvc.perform(get("/api/transaction/category/income"));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("지출 카테고리 목록 조회 성공")
    void getExpenseCategoryList() throws Exception {
        // given
        List<CategoryResponseDto> list = new ArrayList<>();
        ExpenseCategory category = new ExpenseCategory(categoryName);
        list.add(new CategoryResponseDto(category));

        given(categoryService.getExpenseCategories()).willReturn(list);

        // when
        ResultActions result = mockMvc.perform(get("/api/transaction/category/expense"));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("수입 카테고리 삭제 성공")
    void deleteIncomeCategory() throws Exception {
        // given
        doNothing().when(categoryService).deleteIncomeCategory(1L);

        // when
        ResultActions result = mockMvc.perform(patch("/api/transaction/category/income/{categoryId}", 1L)
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk());

        verify(categoryService).deleteIncomeCategory(any());
    }

    @Test
    @DisplayName("수입 카테고리 삭제 실패: 존재하지 않는 카테고리")
    void deleteIncomeCategoryFail() throws Exception {
        // given
        doThrow(TransactionCategoryNotFoundException.class).when(categoryService).deleteIncomeCategory(1L);

        // when
        ResultActions result = mockMvc.perform(patch("/api/transaction/category/income/{categoryId}", 1L)
                .with(csrf())
        );

        // then
        result.andExpect(status().isNotFound());

        verify(categoryService).deleteIncomeCategory(any());
    }

    @Test
    @DisplayName("지출 카테고리 삭제 성공")
    void deleteExpenseCategory() throws Exception {
        // given
        doNothing().when(categoryService).deleteExpenseCategory(1L);

        // when
        ResultActions result = mockMvc.perform(patch("/api/transaction/category/expense/{categoryId}", 1L)
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk());

        verify(categoryService).deleteExpenseCategory(any());
    }

    @Test
    @DisplayName("지출 카테고리 삭제 실패: 존재하지 않는 카테고리")
    void deleteExpenseCategoryFail() throws Exception {
        // given
        doThrow(TransactionCategoryNotFoundException.class).when(categoryService).deleteExpenseCategory(1L);

        // when
        ResultActions result = mockMvc.perform(patch("/api/transaction/category/expense/{categoryId}", 1L)
                .with(csrf())
        );

        // then
        result.andExpect(status().isNotFound());

        verify(categoryService).deleteExpenseCategory(any());
    }
}