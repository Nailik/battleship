package battleship.client.interfaces

import processing.core.PImage
import processing.core.PVector
import battleship.client.elements.Image
import java.util.*

abstract class IViewGroup(position: PVector, background: PImage? = null, size: PVector? = null) : IClickable(position, background, size) {

    var activeView: IView? = null
    var addIViews: MutableList<IView> = Collections.synchronizedList(mutableListOf())
    private var removeIViews = Collections.synchronizedList<IView>(mutableListOf())
    private var activeIViews: MutableList<IView> = Collections.synchronizedList(mutableListOf())

    fun addView(view: IView){
        if(addIViews.contains(view)){
            removeView(view)
        }else {
            view.attach(this)
        }
        addIViews.add(addIViews.size, view)
    }

    fun removeView(IView: IView){
        removeIViews.add(IView)
    }

    override fun update() {
        activeIViews.forEach {
            it.update()
        }
        super.update()
    }

    override fun draw(position: PVector) {
        activeIViews.forEach {
            it.draw()
        }
        activeIViews.removeAll(removeIViews)
        removeIViews.clear()

        activeIViews.addAll(activeIViews.size, addIViews)
        addIViews.clear()
    }

    open fun close() {
        activeView?.isActive = false
        activeIViews.clear()
    }

    override fun onClickedLeft(view: IView){
        if(view != activeView && view !is IScreen) {
            activeView?.isActive = false
            activeView = view
            view.isActive = true
        }
        super.onClickedLeft(view)
    }

}