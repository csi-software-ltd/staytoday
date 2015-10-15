class LastdateUserFilters {

    def filters = {
        all(controller:'administrators',invert:true) {
            before = {

            }
            after = { model ->
                if (model?.user) {                    
                    if(!User.get(model.user?.id?:0)?.isDirty() && !session?.admin?.id){
                        User.get(model.user.id)?.updateLastdate()
                    }
                }
            }
            afterView = {

            }
        }
    }
}