import android.view.*
import org.jetbrains.anko.*

class TimeItemUI : AnkoComponent<ViewGroup> {
    override fun createView(ui: AnkoContext<ViewGroup>): View {
        return with(ui) {
            relativeLayout {
                backgroundColor = 0xfff.opaque
                lparams(matchParent, matchParent)

                textView("TextView") {
                    id = Ids.hourTextView
                    textColor = 0x000.opaque
                    textSize = 24f
                }.lparams(width = wrapContent, height = wrapContent) {
                    alignParentStart()
                    centerVertically()
                }
                textView("TextView") {
                    id = Ids.minutesTextView
                    textColor = 0x000.opaque
                    textSize = 20f
                }.lparams(width = wrapContent, height = wrapContent) {
                    centerVertically()
                }
            }
        }
    }

    private object Ids {
        val hourTextView = View.generateViewId()
        val minutesTextView = View.generateViewId()
    }
}
