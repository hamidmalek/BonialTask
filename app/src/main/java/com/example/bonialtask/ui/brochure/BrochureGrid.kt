package com.example.bonialtask.ui.brochure

import android.location.Location
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bonialtask.model.ContentWrapper

@Composable
fun BrochureGrid(
    data: List<ContentWrapper.Brochure>,
    filterActive: Boolean,
    userLocation: Location?,
    columns: Int,
    modifier: Modifier = Modifier
) {
    val filteredList = remember(filterActive, userLocation, data) {
        if (filterActive && userLocation != null) {
            data.filter { brochure ->
                val store = brochure.content.closestStore
                val results = FloatArray(1)
                Location.distanceBetween(
                    userLocation.latitude,
                    userLocation.longitude,
                    store.latitude,
                    store.longitude,
                    results
                )
                results[0] <= 5_000f
            }
        } else data
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(columns),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(filteredList.size, span = { idx ->
            if (filteredList[idx].contentType == "brochurePremium") StaggeredGridItemSpan.FullLine
            else StaggeredGridItemSpan.SingleLane
        }) { idx ->
            BrochureItem(filteredList[idx])
        }
    }
}
