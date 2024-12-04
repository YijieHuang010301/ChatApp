
The following classes used by the mapping system are not explicitly defined by the OwlMaps library, instead using existing Java library classes.

Below are listed the classes used by the OwlMaps library and their corresponding Google Maps API classes.


java.awt.geom.Point2D   <==> google.maps.Point
	Note:  Point and Point2D.Double are both Point2D entities.    The units of the points default to feet.
	
java.awt.geom.Dimension2D <==> google.maps.Size
	Note: java.awt.Dimension is a Dimension2D entity.  Google's options "widthUnit" an "heightUnit" parameters are not implemented.  The units are assumed to be the same as used for any Point object in the system.

java.awt.geom.Rectangle2D <==> google.maps.Padding
	Note:  Rectangle and Rectangle2D.Double are both Rectangle2D entities.   Padding units are pixels on the screen.
	

	
The following Google Maps entities have no corresponding OwlMaps classes.  All that is needed to define them are a dictionary of options for those entities.

google.maps.Symbol <==> provided.owlMaps.general.ISymbolOptions

google.maps.IconSequence <==> provided.owlMaps.general.IIconSequenceOptions

