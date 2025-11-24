//
//  LoginView.swift
//  DateMate
//
//  Created by 홍희표 on 2021/11/27.
//

import SwiftUI

struct SignInView: View {
    @StateObject private var viewModel = DependencyContainer.instance.provideSignInViewModel()
    
    @FocusState private var focusedField: Field?
    
    var onSignUp: () -> Void
    
    var onForgotPassword: () -> Void
    
    var body: some View {
        let uiState = viewModel.uiState
        
        VStack {
            VStack {
                Logo().padding(.horizontal, 48)
                Text("Find your datemate")
                    .font(.subheadline)
                    .multilineTextAlignment(.center)
                    .padding(.top, 24)
                    .frame(maxWidth: .infinity)
            }
            VStack(alignment: .center) {
                EmailField(
                    value: uiState.email,
                    error: uiState.emailError,
                    onValueChange: viewModel.onEmailChanged,
                    onSubmit: { focusedField = .password }
                )
                .focused($focusedField, equals: .email)
                Spacer().frame(height: 16)
                PasswordField(
                    value: uiState.password,
                    error: uiState.passwordError,
                    onValueChange: viewModel.onPasswordChanged,
                    onSubmit: {
                        viewModel.signIn(email: uiState.email, password: uiState.password)
                    }
                )
                .focused($focusedField, equals: .password)
                Spacer().frame(height: 16)
                Button(action: {
                    viewModel.signIn(email: uiState.email, password: uiState.password)
                }) {
                    if uiState.isLoading {
                        ProgressView().frame(width: 24, height: 24)
                    } else {
                        Text("Sign In")
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(height: 52)
                .background(uiState.isSubmitEnabled && !uiState.isLoading ? Color.blue : Color.gray.opacity(0.4))
                .foregroundColor(.white)
                .cornerRadius(16)
                .disabled(!uiState.isSubmitEnabled || uiState.isLoading)
                if let message = uiState.message {
                    Text(message)
                        .foregroundColor(.red)
                        .font(.caption)
                        .multilineTextAlignment(.center)
                        .padding(.vertical, 8)
                    Spacer().frame(height: 8)
                }
                Button(action: onSignUp) {
                    Text("Sign Up")
                }
                .padding(.top, 8)
                Button(action: onForgotPassword) {
                    Text("Forgot Password?")
                }
                .padding(.top, 16)
            }
            .padding(.horizontal, 16)
            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
    
    enum Field {
        case email
        case password
    }
}

struct Logo: View {
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        Image(colorScheme == .light ? "logo_light_eng" : "logo_dark_eng")
            .resizable()
            .aspectRatio(contentMode: .fit)
            .padding()
    }
}

struct SignInView_Previews: PreviewProvider {
    static var previews: some View {
        SignInView(onSignUp: {}, onForgotPassword: {}).preferredColorScheme(.dark)
    }
}
