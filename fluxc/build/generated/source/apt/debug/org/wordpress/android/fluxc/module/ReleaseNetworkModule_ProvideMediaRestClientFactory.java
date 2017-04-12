package org.wordpress.android.fluxc.module;

import android.content.Context;
import com.android.volley.RequestQueue;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient.Builder;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.UserAgent;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AccessToken;
import org.wordpress.android.fluxc.network.rest.wpcom.media.MediaRestClient;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseNetworkModule_ProvideMediaRestClientFactory implements Factory<MediaRestClient> {
  private final ReleaseNetworkModule module;
  private final Provider<Context> appContextProvider;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<RequestQueue> requestQueueProvider;
  private final Provider<Builder> okHttpClientBuilderProvider;
  private final Provider<AccessToken> tokenProvider;
  private final Provider<UserAgent> userAgentProvider;

  public ReleaseNetworkModule_ProvideMediaRestClientFactory(ReleaseNetworkModule module, Provider<Context> appContextProvider, Provider<Dispatcher> dispatcherProvider, Provider<RequestQueue> requestQueueProvider, Provider<Builder> okHttpClientBuilderProvider, Provider<AccessToken> tokenProvider, Provider<UserAgent> userAgentProvider) {  
    assert module != null;
    this.module = module;
    assert appContextProvider != null;
    this.appContextProvider = appContextProvider;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert requestQueueProvider != null;
    this.requestQueueProvider = requestQueueProvider;
    assert okHttpClientBuilderProvider != null;
    this.okHttpClientBuilderProvider = okHttpClientBuilderProvider;
    assert tokenProvider != null;
    this.tokenProvider = tokenProvider;
    assert userAgentProvider != null;
    this.userAgentProvider = userAgentProvider;
  }

  @Override
  public MediaRestClient get() {  
    MediaRestClient provided = module.provideMediaRestClient(appContextProvider.get(), dispatcherProvider.get(), requestQueueProvider.get(), okHttpClientBuilderProvider.get(), tokenProvider.get(), userAgentProvider.get());
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<MediaRestClient> create(ReleaseNetworkModule module, Provider<Context> appContextProvider, Provider<Dispatcher> dispatcherProvider, Provider<RequestQueue> requestQueueProvider, Provider<Builder> okHttpClientBuilderProvider, Provider<AccessToken> tokenProvider, Provider<UserAgent> userAgentProvider) {  
    return new ReleaseNetworkModule_ProvideMediaRestClientFactory(module, appContextProvider, dispatcherProvider, requestQueueProvider, okHttpClientBuilderProvider, tokenProvider, userAgentProvider);
  }
}

