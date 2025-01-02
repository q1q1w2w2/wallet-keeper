package com.project.wallet_keeper.service;

import com.project.wallet_keeper.domain.*;
import com.project.wallet_keeper.dto.transaction.TransactionDto;
import com.project.wallet_keeper.dto.transaction.TransactionResponseDto;
import com.project.wallet_keeper.exception.transaction.InvalidTransactionOwnerException;
import com.project.wallet_keeper.exception.transaction.TransactionCategoryNotFoundException;
import com.project.wallet_keeper.exception.transaction.TransactionNotFoundException;
import com.project.wallet_keeper.repository.ExpenseCategoryRepository;
import com.project.wallet_keeper.repository.ExpenseRepository;
import com.project.wallet_keeper.repository.IncomeCategoryRepository;
import com.project.wallet_keeper.repository.IncomeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.project.wallet_keeper.domain.Role.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    TransactionService transactionService;

    @Mock
    IncomeRepository incomeRepository;

    @Mock
    ExpenseRepository expenseRepository;

    @Mock
    IncomeCategory incomeCategory;

    @Mock
    ExpenseCategory expenseCategory;

    @Mock
    IncomeCategoryRepository incomeCategoryRepository;

    @Mock
    ExpenseCategoryRepository expenseCategoryRepository;

    @Mock
    User user;

    private static final Long ID = 1L;
    private static final String DETAIL = "월급";
    private static final Integer AMOUNT = 10000;
    private static final String DESCRIPTION = "설명";
    private static final LocalDateTime TRANSACTION_AT = LocalDateTime.of(2000, 1, 1, 0, 0);

    @Test
    @DisplayName("수입 저장 성공")
    void saveIncome() {
        // given
        TransactionDto transactionDto = new TransactionDto(DETAIL, AMOUNT, DESCRIPTION, TRANSACTION_AT, ID);
        Income income = Income.builder()
                .detail(DETAIL)
                .amount(AMOUNT)
                .user(user)
                .incomeAt(TRANSACTION_AT)
                .description(DESCRIPTION)
                .incomeCategory(incomeCategory)
                .build();
        given(incomeRepository.save(any())).willReturn(income);
        given(incomeCategoryRepository.findById(any())).willReturn(Optional.of(incomeCategory));

        // when
        Income saveIncome = transactionService.saveIncome(user, transactionDto);

        // then
        assertThat(saveIncome).isNotNull();
        assertThat(saveIncome.getDetail()).isEqualTo(DETAIL);

        verify(incomeRepository).save(any(Income.class));
    }

    @Test
    @DisplayName("수입 저장 실패: 존재하지 않는 카테고리")
    void saveIncomeFail() {
        // given
        TransactionDto transactionDto = new TransactionDto(DETAIL, AMOUNT, DESCRIPTION, TRANSACTION_AT, ID);

        given(incomeCategoryRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> transactionService.saveIncome(user, transactionDto))
                .isInstanceOf(TransactionCategoryNotFoundException.class);
    }

    @Test
    @DisplayName("지출 저장 성공")
    void saveExpense() {
        // given
        TransactionDto transactionDto = new TransactionDto(DETAIL, AMOUNT, DESCRIPTION, TRANSACTION_AT, ID);
        Expense expense = Expense.builder()
                .detail(DETAIL)
                .amount(AMOUNT)
                .user(user)
                .expenseAt(TRANSACTION_AT)
                .description(DESCRIPTION)
                .expenseCategory(expenseCategory)
                .build();
        given(expenseRepository.save(any())).willReturn(expense);
        given(expenseCategoryRepository.findById(any())).willReturn(Optional.of(expenseCategory));

        // when
        Expense saveExpense = transactionService.saveExpense(user, transactionDto);

        // then
        assertThat(saveExpense).isNotNull();
        assertThat(saveExpense.getDetail()).isEqualTo(DETAIL);

        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    @DisplayName("지출 저장 실패: 존재하지 않는 카테고리")
    void saveExpenseFail() {
        // given
        TransactionDto transactionDto = new TransactionDto(DETAIL, AMOUNT, DESCRIPTION, TRANSACTION_AT, ID);

        given(expenseCategoryRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> transactionService.saveExpense(user, transactionDto))
                .isInstanceOf(TransactionCategoryNotFoundException.class);
    }

    @Test
    @DisplayName("전체 목록 조회 성공")
    void getTransactionList() {
        // given
        Income income = Income.builder()
                .detail(DETAIL)
                .amount(AMOUNT)
                .user(user)
                .incomeAt(TRANSACTION_AT)
                .description(DESCRIPTION)
                .incomeCategory(incomeCategory)
                .build();
        Expense expense = Expense.builder()
                .detail(DETAIL)
                .amount(AMOUNT)
                .user(user)
                .expenseAt(TRANSACTION_AT)
                .description(DESCRIPTION)
                .expenseCategory(expenseCategory)
                .build();

        given(incomeRepository.findByUser(any())).willReturn(Arrays.asList(income));
        given(expenseRepository.findByUser(any())).willReturn(Arrays.asList(expense));

        // when
        List<TransactionResponseDto> transactionList = transactionService.getTransactionList(user);

        // then
        assertThat(transactionList).isNotNull();
        assertThat(transactionList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("지출 목록 조회 성공")
    void getExpenseList() {
        // given
        Expense expense = Expense.builder()
                .detail(DETAIL)
                .amount(AMOUNT)
                .user(user)
                .expenseAt(TRANSACTION_AT)
                .description(DESCRIPTION)
                .expenseCategory(expenseCategory)
                .build();

        given(expenseRepository.findByUser(any())).willReturn(Arrays.asList(expense));

        // when
        List<TransactionResponseDto> expenseList = transactionService.getExpenseList(user);

        // then
        assertThat(expenseList).isNotNull();
        assertThat(expenseList.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("수입 항목 조회 성공")
    void getIncome() {
        // given
        Income income = Income.builder()
                .detail(DETAIL)
                .amount(AMOUNT)
                .user(user)
                .incomeAt(TRANSACTION_AT)
                .description(DESCRIPTION)
                .incomeCategory(incomeCategory)
                .build();

        given(incomeRepository.findById(ID)).willReturn(Optional.of(income));

        // when
        Income findIncome = transactionService.getIncome(ID);

        // then
        assertThat(findIncome).isNotNull();
        assertThat(findIncome.getDetail()).isEqualTo(DETAIL);
    }

    @Test
    @DisplayName("수입 항목 조회 실패: 존재하지 않음")
    void getIncomeFail() {
        // given
        given(incomeRepository.findById(ID)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> transactionService.getIncome(ID))
                .isInstanceOf(TransactionNotFoundException.class);
        verify(incomeRepository).findById(ID);
    }

    @Test
    @DisplayName("지출 항목 조회 성공")
    void getExpense() {
        // given
        Expense expense = Expense.builder()
                .detail(DETAIL)
                .amount(AMOUNT)
                .user(user)
                .expenseAt(TRANSACTION_AT)
                .description(DESCRIPTION)
                .expenseCategory(expenseCategory)
                .build();

        given(expenseRepository.findById(ID)).willReturn(Optional.of(expense));

        // when
        Expense findExpense = transactionService.getExpense(ID);

        // then
        assertThat(findExpense).isNotNull();
        assertThat(findExpense.getDetail()).isEqualTo(DETAIL);
    }

    @Test
    @DisplayName("지출 항목 조회 실패: 존재하지 않음")
    void getExpenseFail() {
        // given
        given(expenseRepository.findById(ID)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> transactionService.getExpense(ID))
                .isInstanceOf(TransactionNotFoundException.class);
        verify(expenseRepository).findById(ID);
    }

    @Test
    @DisplayName("수입 항목 업데이트 성공")
    void updateIncome() {
        // given
        TransactionDto transactionDto = new TransactionDto("updateDetail", 20000, "updateDescription", TRANSACTION_AT, ID);
        Income income = Income.builder()
                .detail(DETAIL)
                .amount(AMOUNT)
                .user(user)
                .incomeAt(TRANSACTION_AT)
                .description(DESCRIPTION)
                .incomeCategory(incomeCategory)
                .build();

        given(incomeRepository.findById(ID)).willReturn(Optional.of(income));
        given(incomeCategoryRepository.findById(any())).willReturn(Optional.of(incomeCategory));

        // when
        Income updateIncome = transactionService.updateIncome(ID, transactionDto, user);

        // then
        assertThat(updateIncome).isNotNull();
        assertThat(updateIncome.getDetail()).isEqualTo("updateDetail");
        assertThat(updateIncome.getAmount()).isEqualTo(20000);
        assertThat(updateIncome.getDescription()).isEqualTo("updateDescription");
    }

    @Test
    @DisplayName("수입 항목 업데이트 실패: 존재하지 않는 수입 항목")
    void updateIncomeFail() {
        // given
        TransactionDto transactionDto = new TransactionDto("updateDetail", 20000, "updateDescription", TRANSACTION_AT, ID);
        given(incomeRepository.findById(ID)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> transactionService.updateIncome(ID, transactionDto, user))
                .isInstanceOf(TransactionNotFoundException.class);
    }

    @Test
    @DisplayName("지출 항목 업데이트 성공")
    void updateExpense() {
        // given
        TransactionDto transactionDto = new TransactionDto("updateDetail", 20000, "updateDescription", TRANSACTION_AT, ID);
        Expense expense = Expense.builder()
                .detail(DETAIL)
                .amount(AMOUNT)
                .user(user)
                .expenseAt(TRANSACTION_AT)
                .description(DESCRIPTION)
                .expenseCategory(expenseCategory)
                .build();

        given(expenseRepository.findById(ID)).willReturn(Optional.of(expense));
        given(expenseCategoryRepository.findById(any())).willReturn(Optional.of(expenseCategory));

        // when
        Expense updateExpense = transactionService.updateExpense(ID, transactionDto, user);

        // then
        assertThat(updateExpense).isNotNull();
        assertThat(updateExpense.getDetail()).isEqualTo("updateDetail");
        assertThat(updateExpense.getAmount()).isEqualTo(20000);
        assertThat(updateExpense.getDescription()).isEqualTo("updateDescription");
    }

    @Test
    @DisplayName("지출 항목 업데이트 실패: 존재하지 않는 지출 항목")
    void updateExpenseFail() {
        // given
        TransactionDto transactionDto = new TransactionDto("updateDetail", 20000, "updateDescription", TRANSACTION_AT, ID);
        given(expenseRepository.findById(ID)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> transactionService.updateExpense(ID, transactionDto, user))
                .isInstanceOf(TransactionNotFoundException.class);
    }

    @Test
    @DisplayName("수입 항목 삭제 성공")
    void deleteIncome() {
        // given
        Income income = Income.builder()
                .detail(DETAIL)
                .amount(AMOUNT)
                .user(user)
                .incomeAt(TRANSACTION_AT)
                .description(DESCRIPTION)
                .incomeCategory(incomeCategory)
                .build();

        given(incomeRepository.findById(ID)).willReturn(Optional.of(income));

        // when
        transactionService.deleteIncome(ID, user);

        // then
        verify(incomeRepository).delete(income);
    }

    @Test
    @DisplayName("수입 항목 삭제 실패: 존재하지 않는 수입 항목")
    void deleteIncomeFail() {
        // given
        given(incomeRepository.findById(ID)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> transactionService.deleteIncome(ID, user))
                .isInstanceOf(TransactionNotFoundException.class);
    }

    @Test
    @DisplayName("수입 항목 삭제 실패: 해당 사용자의 수입이 아닌 경우")
    void deleteIncomeFail2() {
        // given
        Income income = Income.builder()
                .detail(DETAIL)
                .amount(AMOUNT)
                .user(user)
                .incomeAt(TRANSACTION_AT)
                .description(DESCRIPTION)
                .incomeCategory(incomeCategory)
                .build();
        given(incomeRepository.findById(ID)).willReturn(Optional.of(income));

        User otherUser = User.builder()
                .email("other")
                .password("pwd")
                .nickname("other")
                .birth(LocalDate.now())
                .role(ROLE_USER)
                .build();

        // when & then
        assertThatThrownBy(() -> transactionService.deleteIncome(ID, otherUser))
                .isInstanceOf(InvalidTransactionOwnerException.class);
    }

    @Test
    @DisplayName("지출 항목 삭제 성공")
    void deleteExpense() {
        // given
        Expense expense = Expense.builder()
                .detail(DETAIL)
                .amount(AMOUNT)
                .user(user)
                .expenseAt(TRANSACTION_AT)
                .description(DESCRIPTION)
                .expenseCategory(expenseCategory)
                .build();
        given(expenseRepository.findById(ID)).willReturn(Optional.of(expense));

        // when
        transactionService.deleteExpense(ID, user);

        // then
        verify(expenseRepository).delete(expense);
    }

    @Test
    @DisplayName("지출 항목 삭제 실패: 존재하지 않는 지출 항목")
    void deleteExpenseFail() {
        // given
        given(expenseRepository.findById(ID)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> transactionService.deleteExpense(ID, user))
                .isInstanceOf(TransactionNotFoundException.class);
    }

    @Test
    @DisplayName("지출 항목 삭제 실패: 해당 사용자의 지출이 아닌 경우")
    void deleteExpenseFail2() {
        // given
        Expense expense = Expense.builder()
                .detail(DETAIL)
                .amount(AMOUNT)
                .user(user)
                .expenseAt(TRANSACTION_AT)
                .description(DESCRIPTION)
                .expenseCategory(expenseCategory)
                .build();
        given(expenseRepository.findById(ID)).willReturn(Optional.of(expense));

        User otherUser = User.builder()
                .email("other")
                .password("pwd")
                .nickname("other")
                .birth(LocalDate.now())
                .role(ROLE_USER)
                .build();

        // when & then
        assertThatThrownBy(() -> transactionService.deleteExpense(ID, otherUser))
                .isInstanceOf(InvalidTransactionOwnerException.class);
    }
}