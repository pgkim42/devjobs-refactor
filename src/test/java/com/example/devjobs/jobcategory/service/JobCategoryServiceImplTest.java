package com.example.devjobs.jobcategory.service;

import com.example.devjobs.jobcategory.dto.JobCategoryDto;
import com.example.devjobs.jobcategory.entity.JobCategory;
import com.example.devjobs.jobcategory.repository.JobCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobCategoryServiceImplTest {

    @Mock
    private JobCategoryRepository jobCategoryRepository;
    
    @InjectMocks
    private JobCategoryServiceImpl jobCategoryService;
    
    private JobCategory mockCategory;
    private JobCategoryDto.Request request;
    
    @BeforeEach
    void setUp() {
        mockCategory = JobCategory.builder()
                .id(1L)
                .categoryName("백엔드 개발자")
                .build();
                
        request = new JobCategoryDto.Request();
        // Request는 setter가 없으므로 reflection으로 설정
        setField(request, "categoryName", "백엔드 개발자");
    }
    
    @Test
    @DisplayName("initJobCategories - DB가 비어있을 때 초기 카테고리 생성")
    void initJobCategories_WhenDbEmpty_CreateCategories() {
        // given
        when(jobCategoryRepository.count()).thenReturn(0L);
        
        // when
        jobCategoryService.initJobCategories();
        
        // then
        verify(jobCategoryRepository, times(1)).count();
        verify(jobCategoryRepository, times(1)).saveAll(anyList());
    }
    
    @Test
    @DisplayName("initJobCategories - DB에 데이터가 있으면 생성하지 않음")
    void initJobCategories_WhenDbNotEmpty_DoNotCreateCategories() {
        // given
        when(jobCategoryRepository.count()).thenReturn(5L);
        
        // when
        jobCategoryService.initJobCategories();
        
        // then
        verify(jobCategoryRepository, times(1)).count();
        verify(jobCategoryRepository, never()).saveAll(anyList());
    }
    
    @Test
    @DisplayName("createCategory - 카테고리 생성 성공")
    void createCategory_Success() {
        // given
        when(jobCategoryRepository.save(any(JobCategory.class))).thenReturn(mockCategory);
        
        // when
        JobCategoryDto.Response response = jobCategoryService.createCategory(request);
        
        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("백엔드 개발자", response.getCategoryName());
        verify(jobCategoryRepository, times(1)).save(any(JobCategory.class));
    }
    
    @Test
    @DisplayName("getAllCategories - 모든 카테고리 조회 성공")
    void getAllCategories_Success() {
        // given
        List<JobCategory> categories = Arrays.asList(
                JobCategory.builder().id(1L).categoryName("백엔드 개발자").build(),
                JobCategory.builder().id(2L).categoryName("프론트엔드 개발자").build()
        );
        when(jobCategoryRepository.findAll()).thenReturn(categories);
        
        // when
        List<JobCategoryDto.Response> responses = jobCategoryService.getAllCategories();
        
        // then
        assertEquals(2, responses.size());
        assertEquals("백엔드 개발자", responses.get(0).getCategoryName());
        assertEquals("프론트엔드 개발자", responses.get(1).getCategoryName());
    }
    
    @Test
    @DisplayName("updateCategory - 카테고리 수정 성공")
    void updateCategory_Success() {
        // given
        JobCategoryDto.Request updateRequest = new JobCategoryDto.Request();
        setField(updateRequest, "categoryName", "풀스택 개발자");
        
        when(jobCategoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(jobCategoryRepository.save(any(JobCategory.class))).thenReturn(mockCategory);
        
        // when
        JobCategoryDto.Response response = jobCategoryService.updateCategory(1L, updateRequest);
        
        // then
        assertNotNull(response);
        verify(jobCategoryRepository, times(1)).findById(1L);
        verify(jobCategoryRepository, times(1)).save(any(JobCategory.class));
    }
    
    @Test
    @DisplayName("updateCategory - 존재하지 않는 카테고리 수정 시 예외 발생")
    void updateCategory_NotFound_ThrowsException() {
        // given
        when(jobCategoryRepository.findById(999L)).thenReturn(Optional.empty());
        
        // when & then
        assertThrows(EntityNotFoundException.class, 
            () -> jobCategoryService.updateCategory(999L, request));
        
        verify(jobCategoryRepository, times(1)).findById(999L);
        verify(jobCategoryRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("deleteCategory - 카테고리 삭제 성공")
    void deleteCategory_Success() {
        // given
        when(jobCategoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(jobCategoryRepository).deleteById(1L);
        
        // when
        assertDoesNotThrow(() -> jobCategoryService.deleteCategory(1L));
        
        // then
        verify(jobCategoryRepository, times(1)).existsById(1L);
        verify(jobCategoryRepository, times(1)).deleteById(1L);
    }
    
    @Test
    @DisplayName("deleteCategory - 존재하지 않는 카테고리 삭제 시 예외 발생")
    void deleteCategory_NotFound_ThrowsException() {
        // given
        when(jobCategoryRepository.existsById(999L)).thenReturn(false);
        
        // when & then
        assertThrows(EntityNotFoundException.class, 
            () -> jobCategoryService.deleteCategory(999L));
        
        verify(jobCategoryRepository, times(1)).existsById(999L);
        verify(jobCategoryRepository, never()).deleteById(anyLong());
    }
    
    // Reflection을 사용하여 private field 설정
    private void setField(Object object, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}