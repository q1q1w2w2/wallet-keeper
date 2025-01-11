package com.project.wallet_keeper.service;

import com.project.wallet_keeper.domain.*;
import com.project.wallet_keeper.dto.transaction.RegularTransactionResponseDto;
import com.project.wallet_keeper.dto.transaction.TransactionDto;
import com.project.wallet_keeper.exception.transaction.InvalidTransactionOwnerException;
import com.project.wallet_keeper.exception.transaction.TransactionCategoryNotFoundException;
import com.project.wallet_keeper.exception.transaction.TransactionNotFoundException;
import com.project.wallet_keeper.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionSchedulerTest {

    @InjectMocks
    private TransactionScheduler transactionScheduler;

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private IncomeCategoryRepository incomeCategoryRepository;

    @Mock
    private RegularIncomeRepository regularIncomeRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Mock
    private RegularExpenseRepository regularExpenseRepository;

    @Mock
    private User user;

    private static String detail = "내용";
    private static int amount = 100000;
    private static LocalDateTime date = LocalDateTime.of(2000, 1, 1, 0, 0);
    private static Long categoryId = 1L;

    @Test
    @DisplayName("정기 수입 저장 성공")
    void saveRegularIncome() {
        // given
        TransactionDto transactionDto = new TransactionDto(detail, amount, null, date, categoryId);
        IncomeCategory category = createIncomeCategory();
        RegularIncome regularIncome = createRegularIncome();

        given(incomeCategoryRepository.findById(any())).willReturn(Optional.of(category));
        given(regularIncomeRepository.save(any())).willReturn(regularIncome);

        // when
        RegularIncome saveRegularIncome = transactionScheduler.saveRegularIncome(user, transactionDto);

        // then
        assertThat(saveRegularIncome).isNotNull();
        assertThat(saveRegularIncome.getDetail()).isEqualTo(detail);
    }

    @Test
    @DisplayName("정기 수입 저장 실패: 존재하지 않는 카테고리")
    void saveRegularIncomeFail() {
        // given
        TransactionDto transactionDto = new TransactionDto(detail, amount, null, date, categoryId);

        given(incomeCategoryRepository.findById(any())).willThrow(TransactionCategoryNotFoundException.class);

        // when & then
        assertThatThrownBy(() -> transactionScheduler.saveRegularIncome(user, transactionDto))
                .isInstanceOf(TransactionCategoryNotFoundException.class);
    }

    @Test
    @DisplayName("정기 지출 저장 성공")
    void saveRegularExpense() {
        // given
        TransactionDto transactionDto = new TransactionDto(detail, amount, null, date, categoryId);
        ExpenseCategory category = createExpenseCategory();
        RegularExpense regularExpense = createRegularExpense();

        given(expenseCategoryRepository.findById(any())).willReturn(Optional.of(category));
        given(regularExpenseRepository.save(any())).willReturn(regularExpense);

        // when
        RegularExpense saveRegularExpense = transactionScheduler.saveRegularExpense(user, transactionDto);

        // then
        assertThat(saveRegularExpense).isNotNull();
        assertThat(saveRegularExpense.getDetail()).isEqualTo(detail);
    }

    @Test
    @DisplayName("정기 지출 저장 실패: 존재하지 않는 카테고리")
    void saveRegularExpenseFail() {
        // given
        TransactionDto transactionDto = new TransactionDto(detail, amount, null, date, categoryId);

        given(expenseCategoryRepository.findById(any())).willThrow(TransactionCategoryNotFoundException.class);

        // when & then
        assertThatThrownBy(() -> transactionScheduler.saveRegularExpense(user, transactionDto))
                .isInstanceOf(TransactionCategoryNotFoundException.class);
    }

    @Test
    @DisplayName("정기 거래 목록 조회 성공")
    void getRegularTransactions() {
        // given
        List<RegularIncome> regularIncomes = new ArrayList<>();
        List<RegularExpense> regularExpenses = new ArrayList<>();
        regularIncomes.add(createRegularIncome());
        regularExpenses.add(createRegularExpense());

        given(regularIncomeRepository.findAllByUser(any())).willReturn(regularIncomes);
        given(regularExpenseRepository.findAllByUser(any())).willReturn(regularExpenses);

        // when
        List<RegularTransactionResponseDto> regularTransactions = transactionScheduler.getRegularTransactions(user);

        // then
        assertThat(regularTransactions).isNotNull();
        assertThat(regularTransactions.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("정기 수입 삭제 성공")
    void deleteRegularIncome() {
        // given
        RegularIncome regularIncome = createRegularIncome();

        given(regularIncomeRepository.findById(any())).willReturn(Optional.of(regularIncome));
        doNothing().when(regularIncomeRepository).delete(any());

        // when
        transactionScheduler.deleteRegularIncome(user, regularIncome.getId());

        // then
        verify(regularIncomeRepository).delete(regularIncome);
    }

    @Test
    @DisplayName("정기 수입 삭제 실패: 존재하지 않는 항목")
    void deleteRegularIncomeFail() {
        // given
        given(regularIncomeRepository.findById(any())).willThrow(TransactionNotFoundException.class);

        // when & then
        assertThatThrownBy(() -> transactionScheduler.deleteRegularIncome(user, any()))
                .isInstanceOf(TransactionNotFoundException.class);
    }

    @Test
    @DisplayName("정기 수입 삭제 실패: 해당 항목의 소유자가 아님")
    void deleteRegularIncomeFail2() {
        // given
        User otherUser = mock(User.class);
        RegularIncome regularIncome = mock(RegularIncome.class);

        given(regularIncome.getUser()).willReturn(otherUser);
        given(regularIncomeRepository.findById(any())).willReturn(Optional.of(regularIncome));

        // when & then
        assertThatThrownBy(() -> transactionScheduler.deleteRegularIncome(user, 1L))
                .isInstanceOf(InvalidTransactionOwnerException.class);
    }

    @Test
    @DisplayName("정기 지출 삭제 성공")
    void deleteRegularExpense() {
        // given
        RegularExpense regularExpense = createRegularExpense();

        given(regularExpenseRepository.findById(any())).willReturn(Optional.of(regularExpense));
        doNothing().when(regularExpenseRepository).delete(any());

        // when
        transactionScheduler.deleteRegularExpense(user, regularExpense.getId());

        // then
        verify(regularExpenseRepository).delete(regularExpense);
    }

    @Test
    @DisplayName("정기 지출 삭제 실패: 존재하지 않는 항목")
    void deleteRegularExpenseFail() {
        // given
        given(regularExpenseRepository.findById(any())).willThrow(TransactionNotFoundException.class);

        // when & then
        assertThatThrownBy(() -> transactionScheduler.deleteRegularExpense(user, any()))
                .isInstanceOf(TransactionNotFoundException.class);
    }

    @Test
    @DisplayName("정기 지출 삭제 실패: 해당 항목의 소유자가 아님")
    void deleteRegularExpenseFail2() {
        // given
        User otherUser = mock(User.class);
        RegularExpense regularExpense = mock(RegularExpense.class);

        given(regularExpense.getUser()).willReturn(otherUser);
        given(regularExpenseRepository.findById(any())).willReturn(Optional.of(regularExpense));

        // when & then
        assertThatThrownBy(() -> transactionScheduler.deleteRegularExpense(user, 1L))
                .isInstanceOf(InvalidTransactionOwnerException.class);
    }

    @Test
    @DisplayName("정기 수입 수정 성공")
    void updateRegularIncome() {
        // given
        TransactionDto transactionDto = new TransactionDto(detail, amount, null, date, categoryId);
        RegularIncome regularIncome = createRegularIncome();
        IncomeCategory incomeCategory = createIncomeCategory();

        given(regularIncomeRepository.findById(any())).willReturn(Optional.of(regularIncome));
        given(incomeCategoryRepository.findById(any())).willReturn(Optional.of(incomeCategory));

        // when
        RegularIncome updateRegularIncome = transactionScheduler.updateRegularIncome(user, 1L, transactionDto);

        // then
        assertThat(updateRegularIncome).isNotNull();
        assertThat(updateRegularIncome.getDetail()).isEqualTo(detail);
    }

    @Test
    @DisplayName("정기 수입 수정 실패: 존재하지 않는 항목")
    void updateRegularIncomeFail() {
        // given
        TransactionDto transactionDto = new TransactionDto(detail, amount, null, date, categoryId);

        given(regularIncomeRepository.findById(any())).willThrow(TransactionNotFoundException.class);

        // when & then
        assertThatThrownBy(() -> transactionScheduler.updateRegularIncome(user, 1L, transactionDto))
                .isInstanceOf(TransactionNotFoundException.class);
    }

    @Test
    @DisplayName("정기 수입 수정 실패: 존재하지 않는 카테고리")
    void updateRegularIncomeFail2() {
        // given
        TransactionDto transactionDto = new TransactionDto(detail, amount, null, date, categoryId);
        RegularIncome regularIncome = createRegularIncome();

        given(regularIncomeRepository.findById(any())).willReturn(Optional.of(regularIncome));
        given(incomeCategoryRepository.findById(any())).willThrow(TransactionCategoryNotFoundException.class);

        // when & then
        assertThatThrownBy(() -> transactionScheduler.updateRegularIncome(user, 1L, transactionDto))
                .isInstanceOf(TransactionCategoryNotFoundException.class);
    }

    @Test
    @DisplayName("정기 지출 수정 성공")
    void updateRegularExpense() {
        // given
        TransactionDto transactionDto = new TransactionDto(detail, amount, null, date, categoryId);
        RegularExpense regularExpense = createRegularExpense();
        ExpenseCategory expenseCategory = createExpenseCategory();

        given(regularExpenseRepository.findById(any())).willReturn(Optional.of(regularExpense));
        given(expenseCategoryRepository.findById(any())).willReturn(Optional.of(expenseCategory));

        // when
        RegularExpense updateRegularExpense = transactionScheduler.updateRegularExpense(user, 1L, transactionDto);

        // then
        assertThat(updateRegularExpense).isNotNull();
        assertThat(updateRegularExpense.getDetail()).isEqualTo(detail);
    }

    @Test
    @DisplayName("정기 지출 수정 실패: 존재하지 않는 항목")
    void updateRegularExpenseFail() {
        // given
        TransactionDto transactionDto = new TransactionDto(detail, amount, null, date, categoryId);

        given(regularExpenseRepository.findById(any())).willThrow(TransactionNotFoundException.class);

        // when & then
        assertThatThrownBy(() -> transactionScheduler.updateRegularExpense(user, 1L, transactionDto))
                .isInstanceOf(TransactionNotFoundException.class);
    }

    @Test
    @DisplayName("정기 지출 수정 실패: 존재하지 않는 카테고리")
    void updateRegularExpenseFail2() {
        // given
        TransactionDto transactionDto = new TransactionDto(detail, amount, null, date, categoryId);
        RegularExpense regularExpense = createRegularExpense();

        given(regularExpenseRepository.findById(any())).willReturn(Optional.of(regularExpense));
        given(expenseCategoryRepository.findById(any())).willThrow(TransactionCategoryNotFoundException.class);

        // when & then
        assertThatThrownBy(() -> transactionScheduler.updateRegularExpense(user, 1L, transactionDto))
                .isInstanceOf(TransactionCategoryNotFoundException.class);
    }

    @Test
    @DisplayName("정기적으로 수입 저장 로직 성공")
    void testSaveRegularIncomes() {
        // given
        List<RegularIncome> regularIncomes = new ArrayList<>();
        RegularIncome regularIncome = createRegularIncome();
        regularIncomes.add(regularIncome);
        regularIncomes.add(regularIncome);

        given(regularIncomeRepository.findAll()).willReturn(regularIncomes);

        // when
        transactionScheduler.saveRegularIncomes();

        // then
        verify(incomeRepository, times(2)).save(any(Income.class));
    }

    @Test
    @DisplayName("정기적으로 지출 저장 로직 성공")
    void testSaveRegularExpenses() {
        // given
        List<RegularExpense> regularExpenses = new ArrayList<>();
        RegularExpense regularExpense = createRegularExpense();
        regularExpenses.add(regularExpense);
        regularExpenses.add(regularExpense);

        given(regularExpenseRepository.findAll()).willReturn(regularExpenses);

        // when
        transactionScheduler.saveRegularExpenses();

        // then
        verify(expenseRepository, times(2)).save(any(Expense.class));
    }

    private RegularIncome createRegularIncome() {
        return RegularIncome.builder()
                .user(user)
                .detail(detail)
                .amount(amount)
                .incomeCategory(createIncomeCategory())
                .incomeAt(date)
                .build();
    }

    private RegularExpense createRegularExpense() {
        return RegularExpense.builder()
                .user(user)
                .detail(detail)
                .amount(amount)
                .expenseCategory(createExpenseCategory())
                .expenseAt(date)
                .build();
    }

    private ExpenseCategory createExpenseCategory() {
        return new ExpenseCategory("카테고리");
    }

    private IncomeCategory createIncomeCategory() {
        return new IncomeCategory("카테고리");
    }
}