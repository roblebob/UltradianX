# UltradianX  _(ud801_CapStone)_

The _Ultradian eXplorer_ is an Android app, 
    aiming to serve as a mindful tracking tool of one's daily routines.

<br/>

## Background

As a proposed foundation of strategic belief, 
    that is, ___if you want to acquire certain skills most efficiently, 
    you have to work on them daily,
    even when touched only briefly___  (< 10 min), 
    so you can make a habit out of them, or at least invite to the idea:
    _"You are an explorer exploring that particular domain."_
Thereby we shall call each individual domain or endeavour, 
    an __Adventure__.

Inspired by the concept of _ultradian rhythms_, 
    those &#8776; 90 min partitions of the circadian cycle,
    determining the experience within each moment, 
    the user may find it useful to explore the idea, 
    as the app's purpose is to present how time is spent. 

Not to suggest to change any habits, or to even give any advise. 
Just that one may experience them more consciously.
So first, perhaps to grasp what one's habits and needs actually are, 
    and by that eventual deepen the understanding of who oneself is.
And secondly, as a help to not get lost, 
    when being immersed within some particular thing, and thereby forgetting about all the rest. 

As a productivity hack, it has been known to be beneficial
    to start the day with what seems to be most important, we could say that _Adventure_ having the
    highest _Priority_.
However, when it's difficult to start with the most important thing,
    and one risks to end up doing nothing, one might contra with the idea of starting with the
    most exciting thing, and thereby getting into the flow or even better:
    ___Let the present moment decide what the next might be___,
    _(free scheduler, like in free jazz)_

If you have started some domain and you notice that proceeding it,
    serves fun and interest, continue doing it, as to say gives it a higher degree of importance,
    and most importantly a drive of its own.

Furthermore, its target is to weaken that wall that appears 
    when something worth pursuing has not been pursued for a while.
A useful dichotomy might be that overall the app tries to help with _atelic_ pursues which have no 
    explicit endpoint, in contrast to _telic_ ones, which do.

<br/>

## Strategies 

+ the app includes a [___24h spiral activity clock___](#24h-spiral-activity-clock),
  which appearance and location is different, dependent on the users whereabouts within the app.
  So navigation through the app is done by swiping up and downwards on the clocks surface. 

  

+ uses ___Clockify API___, so the app can be regarded as a Clockify client or remote control
   

+ each _Adventure_ has a property called ___Priority___, 
  which gives order to the list in which all _Adventures_ are presented.


+ As it is intrinsically an accumulative property, the dynamics of each individual _Priority_ can be 
  updated in various ways, determined by the user, who is provided with a set of Possibilities,
  reflecting different use cases.


+ Every _Adventure_ has a single Tag, which is used to determine the color of the _Adventure_.
  This color is used to color the _Adventure_ in the list of _Adventures_ and in the
  _24h spiral activity clock_. In general, the Tag is used to group _Adventures_, so that the user
  is invited to have a certain diversity of _Adventures_.




<!-- TODO: ???? 
+ so have th advantages of a daily routine, continuously working something, 
  without the drawbacks, that only a limited amount can be handled daily
 -->

<br/>

## Prototype

At the present moment, there is a prototype which roughly implements some strategies mentioned,
or at least tries to give some foundation for them. 
From that, some of the strategies can be tested. 
Furthermore, some UI mocks are presented below to give some idea of how the app might look like.



### UI Mocks: Overview & Preferences
<img src="/proposal/ui_mocks_overview_preferences_complete.png">

___Left:___ The OverviewFragment shows all _Adventures_ with their respective _Priorities_.
The _Priority_ is represented by the font size and its displacement from the right of the screen.
In short, the bigger the font, and the more it is on the left side, the higher the _Priority_.

___Right:___ The PreferencesFragment shows all the Tags and their associated color, an _Adventures_ 
is dedicated to. Here the user can add, edit and delete Tags, but most important change its color.
Here the user has the most detailed view on the _24h spiral activity clock_.

To navigate from the OverviewFragment to the PreferencesFragment, the user has to swipe upwards on 
the _24h spiral activity clock_. To return from the PreferencesFragment to the OverviewFragment,
the user has to swipe downwards on the _24h spiral activity clock_.

When you click on a particular _Adventure_, you are taken to the MainFragment.


### UI Mocks: Main (_passive/active_)
<img src="/proposal/ui_mocks_main_passive_active2.png">

___Left:___ The MainFragment makes use of ViewPager2 to let the user left/right-swipe through the 
    entire list.
Wheres the color theme presented for each _Adventure_ is determined by the Tag associated with it.
The entirety of the upper half of the screen is dedicated to a multifunctional controller.
The controller is used to start and stop the _Adventure_, 
    when clicking on it, but also like the faded spiral clock in the background, 
    tinted to the tags color suggests, 
    it is used to navigate to the OverviewFragment, when swiping upwards.

___Right:___ When an _Adventure_ is active, which is represented by the ActiveAdventureFragment, 
    the controller is only used to deactivate the _Adventure_, 
    any navigation, like swiping upwards, is disabled.
Also, while being active it is not possible to swipe left/right to navigate through the list of 
    _Adventures_.
The clock itself is glowing golden, pulsating like breathing to indicate that it is active.
Here the user can change the properties of the _Adventure_ itself: 
+ which Tag/category is associated with it.
+ how _Priority_ is calculated 
+ create and edit notes/remarks/comments that the user might find useful to remember.
  (html format -> enables links to resources)




<br/>


## Upcoming implementations



### 24h spiral activity clock

This view is a 24h clock,
updated when some adventure is active, and passive otherwise.
This clock is not only presented within the app,
but can also be used as a widget on the home screen.

<img src="/proposal/ui_mocks_clock.png" width="400">

As the spiral clock only shows the present day, 
a more precise picture of the mock above would be,
presuming it is 10:30 am, 
and the user has been up since 6:00 am:

<img src="/proposal/ui_mocks_overview_preferences_partial.png">


### All the rest of the UI. 

Ideas presented in the mocks above, are not yet implemented within the prototype.


### Business Logic

The workflow to setup the app is,
whenever you start, 
your order the _Adventures_ by their _Priority_ for the entire day cycle, 
starting now.
Since their exists a magnitude of different ways to distribute _Priorities_ 
representing the same ordering, 
finding out their different experiences while synchronizing as a user to the app, 
might be interesting.

To have different Strategies for updating the _Priority_, 
may be key to enable different use cases.


### Clockify API

Although some basics are already implemented within the presented prototype, 
they need to be generalized to new users, 
including account management and workspaces within clockify. 
At present, the communication to the clockify server is only one sided, 
which might be subject to change. 


### Paid vs. Free version (_maybe!_)

In the Free version, like in Reaper, 
when starting the app, 
there is a info page showing that this is a trial version, 
in which the trial period is unlimited, 
but the user is presented with a google advertising frame 
showing until the user touches the screen.


### Testing

The app should include not only unit tests, but also UI tests (Instrumented tests). 

