package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.api.FAQItemService
import javax.inject.Inject

class FAQItemRepository @Inject constructor(
    private val faqService: FAQItemService
) {
    suspend fun getFAQItems() = faqService.getFAQItems()
}