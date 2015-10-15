// Place your Spring DSL code here
import org.springframework.aop.scope.ScopedProxyFactoryBean

beans = {
    requestServiceProxy(ScopedProxyFactoryBean) {
        targetBeanName = 'requestService'
        proxyTargetClass = true
	}
    usersServiceProxy(ScopedProxyFactoryBean) {
        targetBeanName = 'usersService'
        proxyTargetClass = true
	}
  localeResolver(org.springframework.web.servlet.i18n.SessionLocaleResolver) {
      defaultLocale = new Locale("ru","RU")
      java.util.Locale.setDefault(defaultLocale)
   }
}