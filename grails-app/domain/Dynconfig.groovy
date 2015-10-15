class Dynconfig {
  static mapping = {
    version false
  }

  Integer id
  String name
  String value

/////////////////////////////////////////////////////////////////////////////////////////////
	Dynconfig updateValue(_newvalue){
		value = _newvalue.toString()
		this
	}

}