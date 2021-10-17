package battleship.client.elements

import processing.core.PVector
import battleship.client.interfaces.IViewGroup
import battleship.client.interfaces.IListElement
import battleship.client.resources.Colors
import battleship.server.data.Orientation

class ListView<T>(
    position: PVector, size: PVector, var data: List<T>, var pageSize: Int = 5, var creator: ((element: T) -> IListElement<T>)
) : IViewGroup(position) {

    private var spaceVertically = 30f
    var elements = mutableListOf<IListElement<T>>()
    var pageIndex = 0


    private val prevBtn = Button(PVector(size.x - 350, size.y - 80), "prev").apply {
        clickedLeft = {
            if(pageIndex > 0) {
                pageIndex--
                updateData(data)
                updateButtonsEnabled()
            }
        }
    }

    private val nextBtn = Button(PVector(size.x - 100, size.y - 80), "next").apply {
        clickedLeft = {
            if((pageIndex + 1) * pageSize < data.size ) {
                pageIndex++
                updateData(data)
                updateButtonsEnabled()
            }
        }
    }

    init {
        updateData(data)
        addView(prevBtn)
        addView(nextBtn)
        updateButtonsEnabled()
    }

    private fun updateButtonsEnabled(){
        prevBtn.isEnabled = pageIndex > 0
        nextBtn.isEnabled = (pageIndex + 1) * pageSize < data.size
    }

    fun updateData(data: List<T>) {
        this.data = data

        for (i in pageIndex * pageSize until (pageIndex + 1) * pageSize) {
            val viewIndex = i - (pageIndex * pageSize)

            if (i < data.size) {
                if (viewIndex >= elements.size) {
                    //create new element
                    elements.add(creator(data[i]).also {
                        it.setPositionOffset(viewIndex, spaceVertically)
                        addView(it)
                        //refresh it
                        it.update(it.data)
                    })
                } else {
                    //update data
                    elements[viewIndex].update(data[i])
                    elements[viewIndex].isVisible = true
                }
            } else {
                if(viewIndex < elements.size) {
                    elements[viewIndex].isVisible = false
                }
            }
        }

        updateButtonsEnabled()
    }
}