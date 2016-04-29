#include <stdio.h>
#include <stdlib.h>

void swap(int* x, int* y);
void quicksort(int list[], int m, int n);

void main()
{
    const int SIZE = 100000000;
    int* list = malloc(SIZE * sizeof(int));

    for(int i=0; i<SIZE; i++)
        list[i] = i;
    
    for(int i = SIZE - 1; i > 0; i -= 1) {
        int index = rand() % (i + 1);
        swap(&list[index], &list[i]);
    }
    
    /* sort the list using quicksort algorithm */
    quicksort(list, 0, SIZE);
}

void swap(int* x, int* y)
{
    int temp;
    temp = *x;
    *x = *y;
    *y = temp;
}

int findPivot(int begin, int end) {
    return (begin + end) / 2;
}

void quicksort(int s[], int begin, int end) {
    int pivot = findPivot(begin, end);
    int left = begin;
    int right = end - 1;
    while(left <= right) {
        left += s[left] < pivot;
        right -= s[right] > pivot;
        left += s[left] < pivot;
        right -= s[right] > pivot;
        
        if(s[left] >= pivot && s[right] <= pivot && left <= right) {
            int temp = s[left];
            s[left] = s[right];
            s[right] = temp;
            left += 1;
            right -= 1;
        }
    }
    int sortLeft = begin < right;
    int sortRight = left < (end - 1);
    
    if(sortLeft)
        quicksort(s, begin, right + 1);
    if(sortRight)
        quicksort(s, left, end);
}